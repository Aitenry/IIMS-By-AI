package com.toryu.iims.ai.chat.service.impl;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import com.toryu.iims.ai.rag.even.DocumentEmbeddingEvent;
import com.toryu.iims.ai.rag.factory.DocTypeService;
import com.toryu.iims.ai.rag.factory.impl.DocTypeServiceFactory;
import com.toryu.iims.ai.rag.model.entity.DocumentType;
import com.toryu.iims.ai.chat.model.entity.ModelSetting;
import com.toryu.iims.ai.chat.service.AiChatSettingService;
import com.toryu.iims.ai.chat.service.ModelToolService;
import com.toryu.iims.ai.chat.service.ModelService;
import com.toryu.iims.ai.rag.utils.PromptTemplateUtil;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.ai.document.Document;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class ModelToolServiceImpl implements ModelToolService {

    private final ModelService modelService;

    private final MinioService minioService;

    private final ApplicationEventPublisher eventPublisher;

    private final DocTypeServiceFactory docTypeServiceFactory;

    private final AiChatSettingService settingService;

    public ModelToolServiceImpl (ModelService modelService, MinioService minioService,
                                 ApplicationEventPublisher eventPublisher, DocTypeServiceFactory docTypeServiceFactory,
                                 AiChatSettingService settingService) {
        this.modelService = modelService;
        this.minioService = minioService;
        this.eventPublisher = eventPublisher;
        this.docTypeServiceFactory = docTypeServiceFactory;
        this.settingService = settingService;
    }

    @Override
    public String imageOcrByModel(List<Long> fileIds) {
        List<Message> messages = new ArrayList<>();
        SystemMessage systemMessage = new SystemMessage(
        """
                你是一名专业的OCR信息提取助手，任务是从图片中识别并提取所有关键信息，且生成的键值对必须是出自于原图片内容，并将其结构化为JSON格式输出。
                请根据图片内容的逻辑层次使用合适嵌套的JSON对象来表示。务必保证所提取的信息忠实于原图片内容，严禁伪造或臆造信息。
                注意：在解析过程中，保持对信息位置和逻辑关系的敏感度，以确保生成的JSON结构尽可能接近原始内容布局。
                """
        );
        ArrayList<Media> media = new ArrayList<>();
        if (!fileIds.isEmpty()) {
            fileIds.forEach(id -> media.add(new Media(MimeTypeUtils.ALL,
                    new InputStreamResource(minioService.getInputStream(id)))));
        }
        UserMessage userMessage = UserMessage.builder().text("输出结构化的JSON内容").media(media).build();
        messages.add(systemMessage);
        messages.add(userMessage);
        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse call = modelService.getChatModel(modelSetting.getModelId())
                .call(new Prompt(messages));
        return call.getResult().getOutput().getText();
    }

    @Override
    public String generateSummaryByModel(DocumentType documentType) {
        Long id = documentType.getId();
        DocumentTypeEnum type = documentType.getType();
        DocTypeService service = docTypeServiceFactory.getService(type);
        List<Document> documents = service.getDocument(id);

        // 定义阈值
        int threshold = 60; // 单次摘要的最大长度
        int maxContentLength = 10000; // 合并内容的最大长度（避免超出模型输入限制）

        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder mergedContentBuilder = new StringBuilder();
        int index = 1;

        for (Document document : documents) {
            String content = Objects.requireNonNull(document.getText());

            // 检查合并内容是否超过最大长度
            if (mergedContentBuilder.length() + content.length() <= maxContentLength) {
                // 如果未超过最大长度，将当前文档内容添加到合并内容中
                mergedContentBuilder.append(content).append("\n");
            } else {
                // 如果超过最大长度，处理已合并的内容
                processMergedContent(mergedContentBuilder, contentBuilder, index, threshold);

                // 清空合并内容，并将当前文档内容作为新的起点
                mergedContentBuilder.setLength(0);
                mergedContentBuilder.append(content).append("\n");
                index++;
            }
        }

        // 处理剩余的合并内容（如果还有未处理的部分）
        if (!mergedContentBuilder.isEmpty()) {
            processMergedContent(mergedContentBuilder, contentBuilder, index, threshold);
        }

        // 根据所有片段摘要生成最终的文档摘要
        Message finalMessage = new PromptTemplate("""
            所有的文档片段摘要都来自于同一个文档，请保持完整性、连贯性、真实性。
            ---所有文档片段摘要----------
            {content}
            ---------------------
            根据上面的文档的所有片段摘要生成一个不超过360个字的文档摘要，直接输出文档摘要。
            """
        ).createMessage(Map.of("content", contentBuilder.toString()));

        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse finalCall = modelService.getChatModel(modelSetting.getModelId())
                .call(new Prompt(finalMessage));
        return PromptTemplateUtil.removeThink(finalCall.getResult().getOutput().getText());
    }

    @Override
    public void documentEmbeddingByModel(Long wikiId, DomProcessEnum domProcess) {
        eventPublisher.publishEvent(new DocumentEmbeddingEvent(this, wikiId, BaseContext.getCurrentId(), domProcess));
    }

    /**
     * 处理合并的内容，生成摘要并追加到结果中
     */
    private void processMergedContent(StringBuilder mergedContentBuilder, StringBuilder contentBuilder, int index, int threshold) {
        String mergedContent = mergedContentBuilder.toString();
        if (StringUtils.isBlank(mergedContent)) return;

        // 调用模型生成摘要
        Message message = new PromptTemplate("""
            请保持完整性、连贯性、真实性，且不能丢失关键描述。
            ---文章片段----------
            {content}
            ---------------------
            根据上面的文档片段生成一个不超过{threshold}个字的摘要，直接输出摘要：
            """
        ).createMessage(Map.of("content", mergedContent, "threshold", threshold));

        ModelSetting modelSetting = settingService.getUserModelSetting();
        ChatResponse call = modelService.getChatModel(modelSetting.getModelId())
                .call(new Prompt(message));
        String result = PromptTemplateUtil.removeThink(call.getResult().getOutput().getText());

        // 将生成的摘要追加到结果中
        String text = "# 当前文档的第" + index + "个片段摘要：";
        contentBuilder.append(text).append(result).append("\n\n");
    }

}
