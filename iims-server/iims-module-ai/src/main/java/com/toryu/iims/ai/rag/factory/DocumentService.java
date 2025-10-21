package com.toryu.iims.ai.rag.factory;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;

public interface DocumentService {
    DomProcessEnum getType();
    Boolean process(Long wikiId);
}