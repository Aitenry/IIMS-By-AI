package com.toryu.iims.ai.rag.utils;

import com.toryu.iims.ai.rag.context.PromptTemplateContext;
import com.toryu.iims.ai.rag.model.entity.RagMessage;
import org.apache.commons.lang.StringUtils;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PromptTemplateUtil {

    public static RagMessage createDocumentMessage(String question, List<String> loadFileContext,
                                                   List<Document> documents, Double score) {
        String wikiContext = "";
        String fileContext = "";
        List<Document> documentsFilter = new ArrayList<>();

        if (Objects.nonNull(documents)) {
            // 过滤文档并将符合条件的文档添加到 documentsFilter
            documentsFilter = documents.stream()
                    .filter(doc -> Optional.of(doc.getScore()).orElse(0.0) > score)
                    .collect(Collectors.toList());

            // 如果过滤后没有符合条件的文档，则选择概率最大的文档
            if (documentsFilter.isEmpty()) {
                Optional<Document> maxScoreDoc = documents.stream()
                        .max(Comparator.comparingDouble(doc -> Optional.of(doc.getScore()).orElse(0.0)));
                maxScoreDoc.ifPresent(documentsFilter::add); // 将概率最大的文档加入 documentsFilter
            }

            // 提取过滤后的文档内容并拼接
            List<String> filteredContent = documentsFilter.stream()
                    .map(Document::getText)
                    .collect(Collectors.toList());
            // 拼接文档内容
            if (!filteredContent.isEmpty()) {
                wikiContext = String.join("\n", filteredContent);
            }
        }

        if (Objects.nonNull(loadFileContext)) {
            fileContext = String.join("\n", loadFileContext);
        }

        // 创建系统提示消息
        Message sysMessage = new SystemPromptTemplate(PromptTemplateContext.RAG_SYSTEM)
                .createMessage(Map.of("wikiContext", wikiContext, "fileContext", fileContext));

        // 构建提示模板
        Message message = new PromptTemplate(PromptTemplateContext.RAG_USER)
                .createMessage(Map.of("question", question));

        return RagMessage.builder()
                .sysMessage(sysMessage).userMessage(message)
                .documents(documentsFilter)
                .build();
    }

    public static void defineMessage(String question, List<String> fileContext, List<Message> messages) {
        if (Objects.nonNull(fileContext) && !fileContext.isEmpty()) {
            String context = String.join("\n", fileContext);
            Message sysMessage = new SystemPromptTemplate(PromptTemplateContext.DEFINE_SYSTEM)
                    .createMessage(Map.of("context", context));
            messages.add(0, sysMessage);
        }
        messages.add(new UserMessage(question));
    }

    public static String removeThink(String context) {
        if (StringUtils.isBlank(context)) return "";
        return context.replaceAll("(?s)<think>.*?</think>\\s*", "");
    }

    public static String removeJsonCodeBlocks(String context) {
        Matcher matcher = Pattern.compile("```json\\s*([\\s\\S]*?)\\s*```").matcher(context);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return context;
    }

}