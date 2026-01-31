package com.toryu.iims.ai.chat.service.impl;

import com.toryu.iims.ai.chat.model.dto.SendMessageDTO;
import com.toryu.iims.ai.chat.model.entity.*;
import com.toryu.iims.ai.chat.service.*;
import com.toryu.iims.ai.chat.utils.AgentUtil;
import com.toryu.iims.ai.chat.utils.ChatUtil;
import com.toryu.iims.ai.rag.enums.FileModelTypeEnum;
import com.toryu.iims.ai.rag.handle.PromptHandlerContext;
import com.toryu.iims.ai.rag.model.entity.RagMessage;
import com.toryu.iims.ai.rag.utils.PromptTemplateUtil;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.enums.AiModelApiType;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import com.toryu.iims.common.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    Map<Long, MessageData> msgMap = new ConcurrentHashMap<>();

    private final ModelService modelService;
    private final AiChatModelsService aiChatModelsService;
    private final TopicManageService topicManageService;
    private final DialogueManageService dialogueManageService;
    private final FileStorageService fileStorageService;
    private final AiChatSettingService settingService;
    private final PromptHandlerContext promptHandlerContext;
    private final ChatUtil chatUtil;
    private final AgentUtil agentUtil;

    public ChatServiceImpl(ModelService modelService, AiChatModelsService aiChatModelsService, TopicManageService topicManageService,
                           DialogueManageService dialogueManageService, FileStorageService fileStorageService,
                           AiChatSettingService settingService, PromptHandlerContext promptHandlerContext, ChatUtil chatUtil, AgentUtil agentUtil) {
        this.modelService = modelService;
        this.aiChatModelsService = aiChatModelsService;
        this.topicManageService = topicManageService;
        this.dialogueManageService = dialogueManageService;
        this.fileStorageService = fileStorageService;
        this.settingService = settingService;
        this.promptHandlerContext = promptHandlerContext;
        this.chatUtil = chatUtil;
        this.agentUtil = agentUtil;
    }

    @Override
    public SseEmitter conversation(Long uuid, SendMessageDTO messageDto) {
        // 检查 MessageData 是否存在
        if (messageDto == null) {
            log.error("MessageData not found for UUID: {}", uuid);
            return chatUtil.createErrorEmitter();
        }
        MessageData messageData = MessageData.builder().sse(new SseEmitter(0L))
                .fileId(messageDto.getFileId()).wikiIds(messageDto.getWikiIds())
                .question(messageDto.getQuestion()).topicId(messageDto.getTopicId()).tools(new ArrayList<>())
                .lastId(messageDto.getLastId()).modelId(messageDto.getModelId()).build();
        Long userId = BaseContext.getCurrentId();
        // 提取必要信息
        SseEmitter emitter = messageData.getSse();
        CompletableFuture.runAsync(() -> {
            BaseContext.setCurrentId(userId);
            String question = messageData.getQuestion();
            Long topicId = messageData.getTopicId();
            if (Objects.isNull(topicId)) {
                String title = chatUtil.limitLengthWithEllipsis(question);
                AiChatTopic aiChatTopic = AiChatTopic.builder().title(title)
                        .isDeleted(false).build();
                topicManageService.insertTopic(aiChatTopic);
                topicId = aiChatTopic.getId();
                messageData.setTopicId(topicId);
            }
            msgMap.put(uuid, messageData);
            Long fileId = messageData.getFileId();
            List<Long> wikiIds = messageData.getWikiIds();
            String _uuid = String.valueOf(uuid);
            try {

                // 加载历史消息或文档内容，并不携带最新的用户问题，由构建Prompt时加载
                List<Message> messages = dialogueManageService.loadingDialogueHistory(topicId);

                // 构建并插入用户对话记录
                AiChatDialogue userAiChatDialogue = chatUtil.buildUserDialogue(messageData);
                dialogueManageService.insertDialogue(userAiChatDialogue);
                Long lastId = userAiChatDialogue.getId();
                messageData.setLastId(lastId);

                List<String> fileContext = null;
                FileModelTypeEnum type = FileModelTypeEnum.TEXT;
                FileWarehouse object = fileStorageService.getObjectById(fileId);
                if (Objects.nonNull(object)) {
                    ModelUseInfo filePrompt = promptHandlerContext.handleFile(object);
                    type = filePrompt.getType();
                    fileContext = filePrompt.getContext();
                }

                ModelSetting modelSetting = settingService.getUserModelSetting();
                Long modelId = messageData.getModelId();
                ChatApi chatApi = aiChatModelsService.selectModelById(modelId);
                // 发送开始事件
                chatUtil.sendStartEvent(emitter, _uuid, topicId, lastId);

                if (Objects.equals(chatApi.getType(), AiModelApiType.AGENT)) {
                    PromptTemplateUtil.defineMessage(question, fileContext, messages);
                    switch (type) {
                        case IMAGE -> modelId = modelSetting.getVisionModel();
                        case AUDIO, VIDEO -> modelId = modelSetting.getMultimodalModel();
                        default -> modelId = modelSetting.getLanguageModel();
                    }
                    agentUtil.processStream(
                            uuid, userId, modelId, new ArrayList<>(), messages,
                            messageData, emitter, msgMap);
                    return;
                }

                List<Document> documents = null;
                if (Objects.nonNull(wikiIds) && !wikiIds.isEmpty()) {
                    RagMessage ragMessage = chatUtil.loadingWikiDoc(wikiIds, fileContext, question, emitter, _uuid);
                    messages.add(0, ragMessage.getSysMessage());
                    messages.add(ragMessage.getUserMessage());
                    documents = ragMessage.getDocuments();
                } else {
                    PromptTemplateUtil.defineMessage(question, fileContext, messages);
                }
                // 订阅 AI 流并处理数据
                Flux<ChatResponse> stream = modelService.getChatModel(modelId).stream(new Prompt(messages));
                chatUtil.processStream(stream, documents, msgMap, emitter, uuid);

                // 设置断开连接和超时回调
                chatUtil.setupEmitterCallbacks(msgMap, emitter, uuid);

            } catch (Exception e) {
                log.error("[Start] >> ", e);
                this.stopConversation(uuid);
            }
        });
        return emitter;
    }

    @Override
    public SseEmitter regenerate(Long uuid, Long lastId) {
        return null;
    }

    @Override
    public Boolean stopConversation(Long uuid) {
        MessageData messageData = msgMap.get(uuid);
        if (Objects.isNull(messageData)) { return false; }
        chatUtil.saveAiDialogueAndComplete(msgMap, uuid);
        return true;
    }

}
