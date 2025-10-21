package com.toryu.iims.ai.rag.service;

import com.toryu.iims.common.enums.DocumentTypeEnum;
import org.springframework.ai.document.Document;

import java.util.List;

public interface MilvusStoreService {

    List<Document> loadDocumentByWikiReader(List<Long> wikiIds, String question, Integer topK);

    Boolean addDocumentByWiki(Long wikiId);

    void delDocumentByWiki(Long wikiId);

    void delDocumentByDocId(Long docId, DocumentTypeEnum type);

}
