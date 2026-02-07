package com.toryu.iims.ai.rag.even;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DocumentEmbeddingEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private final Long wikiId;

    private final Long currentId;

    private final DomProcessEnum domProcess;

    public DocumentEmbeddingEvent(Object source, Long wikiId, Long currentId, DomProcessEnum domProcess) {
        super(source);
        this.wikiId = wikiId;
        this.currentId = currentId;
        this.domProcess = domProcess;
    }
}