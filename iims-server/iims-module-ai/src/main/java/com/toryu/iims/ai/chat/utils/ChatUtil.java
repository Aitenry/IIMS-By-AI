package com.toryu.iims.ai.chat.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.toryu.iims.ai.chat.model.entity.*;
import com.toryu.iims.ai.chat.model.vo.DocMetadataVO;
import com.toryu.iims.ai.chat.service.AiChatSettingService;
import com.toryu.iims.ai.chat.service.DialogueManageService;
import com.toryu.iims.ai.chat.service.ModelService;
import com.toryu.iims.ai.rag.model.entity.RagMessage;
import com.toryu.iims.ai.rag.service.MilvusStoreService;
import com.toryu.iims.ai.rag.utils.DocMetadataUtil;
import com.toryu.iims.ai.rag.utils.PromptTemplateUtil;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.enums.FileStatusEnum;
import com.toryu.iims.common.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class ChatUtil {

    private final DialogueManageService dialogueManageService;
    private final MilvusStoreService milvusStoreService;
    private final DocMetadataUtil docMetadataUtil;
    private final ModelService modelService;
    private final FileStorageService fileStorageService;

    private final AiChatSettingService settingService;

    public ChatUtil(DialogueManageService dialogueManageService, MilvusStoreService milvusStoreService,
                    DocMetadataUtil docMetadataUtil, ModelService modelService,
                    FileStorageService fileStorageService, AiChatSettingService settingService) {
        this.dialogueManageService = dialogueManageService;
        this.milvusStoreService = milvusStoreService;
        this.docMetadataUtil = docMetadataUtil;
        this.modelService = modelService;
        this.fileStorageService = fileStorageService;
        this.settingService = settingService;
    }

    // 辅助方法：创建错误的 SseEmitter
    public SseEmitter createErrorEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitter.completeWithError(new IllegalArgumentException("Invalid UUID"));
        return emitter;
    }

    // 辅助方法：构建用户对话记录
    public AiChatDialogue buildUserDialogue(MessageData messageData) {
        List<Long> fileIds = messageData.getFileIds();
        AiChatDialogue dialogue = AiChatDialogue.builder()
                .topicId(messageData.getTopicId())
                .lastId(messageData.getLastId())
                .sender("user")
                .content(JSONObject.toJSONString(UserContent.builder()
                        .question(messageData.getQuestion()).build()))
                .isStar(false)
                .isDeleted(false)
                .build();
        if (Objects.nonNull(fileIds) && !fileIds.isEmpty()) {
            fileIds.forEach(fileId -> fileStorageService.updateFileStatus(fileId, FileStatusEnum.USED));
            dialogue.setFileIds(fileIds);
        }
        return dialogue;
    }

    // 辅助方法：发送开始事件
    public void sendStartEvent(SseEmitter emitter, String uuid, Long topicId, Long lastId) throws IOException {
        emitter.send(SseEmitter.event().name("start")
               .id(uuid).data(SendStartData.builder().topicId(topicId)
                        .createTime(LocalDateTime.now()).lastId(lastId).build()));
    }

    // 辅助方法：处理流数据
    public void processStream(Flux<ChatResponse> stream, List<Document> documents,
                              Map<Long, MessageData> msgMap, SseEmitter emitter, Long uuid) {
        MessageData messageData = msgMap.get(uuid);
        List<AiContent> aiContent = new ArrayList<>();
        aiContent.add(AiContent.builder().content(new StringBuffer())
                .thinking(new StringBuffer()).build());
        messageData.setAiContent(aiContent);
        messageData.setDocuments(documents);
        Long topicId = messageData.getTopicId();
        Long userId = BaseContext.getCurrentId();
        SendOutputData outputData = SendOutputData.builder()
                .topicId(topicId).aiContent(aiContent).build();
        stream.subscribe(data -> {
                try {
                    AssistantMessage assistantMessage = data.getResult().getOutput();
                    String text = assistantMessage.getText();
                    if (Objects.nonNull(text)) {
                        aiContent.get(0).getContent().append(text);
                    }
                    emitter.send(SseEmitter.event().name("output")
                           .id(String.valueOf(uuid)).data(outputData));
                } catch (IOException e) {
                    log.error("AI Stream 消息发送出错：", e);
                    BaseContext.setCurrentId(userId);
                    this.saveAiDialogueAndComplete(msgMap, uuid);
                }
            },
            error -> {
                log.error("AI Subscribe 消息发送出错：", error);
                BaseContext.setCurrentId(userId);
                saveAiDialogueAndComplete(msgMap, uuid);
            },
            () -> {
                BaseContext.setCurrentId(userId);
                this.saveAiDialogueAndComplete(msgMap, uuid);
            }
        );
    }

    
    // 辅助方法：设置 Emitter 回调
    public void setupEmitterCallbacks(Map<Long, MessageData> msgMap, SseEmitter emitter, Long uuid) {
        emitter.onCompletion(() -> log.info("SSE 连接已关闭，UUID: {}", uuid));
        emitter.onTimeout(() -> {
            log.warn("SSE 连接超时，UUID: {}", uuid);
            this.saveAiDialogueAndComplete(msgMap, uuid);
        });
    }

    /**
     * 保存 AI 对话记录并完成 SSE 连接
     */
    public void saveAiDialogueAndComplete(Map<Long, MessageData> msgMap, Long uuid) {
        String _uuid = String.valueOf(uuid);
        MessageData messageData = msgMap.get(uuid);
        List<Document> documents = messageData.getDocuments();
        List<ChatTool> tools = messageData.getTools();
        Long topicId = messageData.getTopicId();
        Long lastId = messageData.getLastId();
        List<AiContent> aiContent = messageData.getAiContent();
        SseEmitter emitter = messageData.getSse();
        try {
            // 构建 AI 对话记录
            String metadata = Objects.isNull(documents) ? null : JSONArray.toJSONString(documents);
            String toolsResult = Objects.isNull(tools) ? null : JSONArray.toJSONString(tools);
            String content = Objects.isNull(aiContent) ? "[]" : JSONArray.toJSONString(aiContent);
            AiChatDialogue aiAiChatDialogue = AiChatDialogue.builder()
                    .topicId(topicId).lastId(lastId).sender("assistant")
                    .content(content).isStar(false).metadata(metadata).tools(toolsResult)
                    .isDeleted(false).build();
            Long userId = BaseContext.getCurrentId();
            // 异步保存 AI 对话记录，并等待完成
            CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() -> {
                BaseContext.setCurrentId(userId);
                dialogueManageService.insertDialogue(aiAiChatDialogue);
                log.info("AI 对话记录已保存，ID: {}", aiAiChatDialogue.getId());
            });
            saveFuture.join(); // 等待异步操作完成

            // 尝试发送结束事件
            List<DocMetadataVO> list = docMetadataUtil.getDocMetadata(aiAiChatDialogue.getMetadata());
            emitter.send(SseEmitter.event().name("end")
                    .id(_uuid).data(SendEndData.builder().topicId(topicId)
                            .lastId(aiAiChatDialogue.getId())
                            .docMetadata(list).build()));
        } catch (Exception e) {
            log.error("[End] >> ", e);
        } finally {
            // 确保完成 SSE 连接
            emitter.complete();
            msgMap.remove(uuid);
        }
    }

    public String limitLengthWithEllipsis(String question) {
        final int maxLength = 36;
        if (question == null) {
            return "";
        }
        if (question.length() > maxLength) {
            return question.substring(0, maxLength) + "...";
        } else {
            return question;
        }
    }

    public RagMessage loadingWikiDoc(List<Long> wikiIds, List<String> fileContext, String question, SseEmitter emitter, String _uuid) throws IOException {
        // 判断问题是否过长
        boolean isLong = question.length() > 130;
        int totalSteps = isLong ? 2 : 1;
        int currentStep = 1;

        // 如果问题过长，进行问题简化处理
        String simplifiedQuestion = question;
        if (isLong) {
            this.sendStatusUpdate(emitter, _uuid, "正在对用户问题进行简化...", totalSteps, currentStep);
            // 构建系统提示和用户问题消息
            Message sysMessage = new SystemPromptTemplate("""
            你是一名可以理解用户的问题的助手，你的作用是直接输出用户需要干什么。
            要求：你并不需要处理用户的问题，只是理解并直接输出一句话（只要指令），不能超过60字。
            """).createMessage();
            Message userMessage = new PromptTemplate(question).createMessage();

            // 调用模型简化问题
            ModelSetting modelSetting = settingService.getUserModelSetting();
            ChatResponse response = modelService.getChatModel(modelSetting.getModelId())
                    .call(new Prompt(List.of(sysMessage, userMessage)));
            simplifiedQuestion = PromptTemplateUtil.removeThink(response.getResult().getOutput().getText());
            log.info("简化的内容为：{}", simplifiedQuestion);
            currentStep++;
        }

        // 发送状态更新：正在召回知识库数据
        this.sendStatusUpdate(emitter, _uuid, "正在召回知识库的数据...", totalSteps, currentStep);

        // 加载文档
        List<Document> documents = milvusStoreService.loadDocumentByWikiReader(wikiIds, simplifiedQuestion, 10);

        // 返回处理后的结果
        return PromptTemplateUtil.createDocumentMessage(question, fileContext, documents, 0.50);
    }

    // 提取状态更新逻辑为独立方法
    public void sendStatusUpdate(SseEmitter emitter, String uuid, String task, int total, int progress) throws IOException {
        log.info("{}/{} >> {}", progress, total, task);
        emitter.send(SseEmitter.event().name("status").id(uuid)
                .data(SendStatusData.builder().task(task).total(total).progress(progress).build()));
    }
}
