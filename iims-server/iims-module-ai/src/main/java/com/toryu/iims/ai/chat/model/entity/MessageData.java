package com.toryu.iims.ai.chat.model.entity;

import com.toryu.iims.common.enums.AiApiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageData implements Serializable {

    private Long topicId;

    private Long lastId;

    private Long fileId;

    private Long modelId;

    private AiApiType apiType;

    private List<Long> wikiIds;

    private String question;

    private SseEmitter sse;

    private List<AiContent> aiContent;

    private List<Document> documents;

    private List<ChatTool> tools;

}
