package com.toryu.iims.ai.chat.service;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import com.toryu.iims.ai.rag.model.entity.DocumentType;

import java.util.List;

public interface ModelToolService {

    String imageOcrByModel(List<Long> fileIds);

    String generateSummaryByModel(DocumentType documentType);

    void documentEmbeddingByModel(Long wikiId, DomProcessEnum domProcess);

}
