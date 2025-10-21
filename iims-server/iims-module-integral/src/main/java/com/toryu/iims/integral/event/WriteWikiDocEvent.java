package com.toryu.iims.integral.event;

import com.toryu.iims.common.enums.DocumentTypeEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WriteWikiDocEvent extends ApplicationEvent {

    /**
     * 文章 ID
     */
    private final Long docId;

    private final DocumentTypeEnum type;

    public WriteWikiDocEvent(Object source, Long docId, DocumentTypeEnum type) {
        super(source);
        this.docId = docId;
        this.type = type;
    }

}
