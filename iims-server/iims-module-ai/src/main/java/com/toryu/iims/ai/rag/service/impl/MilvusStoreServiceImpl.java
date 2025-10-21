package com.toryu.iims.ai.rag.service.impl;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import com.toryu.iims.ai.rag.factory.DocumentService;
import com.toryu.iims.ai.rag.factory.impl.DocumentServiceFactory;
import com.toryu.iims.ai.rag.service.CustomizeVectorStoreService;
import com.toryu.iims.ai.rag.service.MilvusStoreService;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class MilvusStoreServiceImpl implements MilvusStoreService {
    private final CustomizeVectorStoreService storeService;
    private final DocumentServiceFactory factory;


    public MilvusStoreServiceImpl(CustomizeVectorStoreService storeService, DocumentServiceFactory factory) {
        this.storeService = storeService;
        this.factory = factory;
    }

    @Override
    public List<Document> loadDocumentByWikiReader(List<Long> wikiIds, String question, Integer topK) {
        if (Objects.isNull(wikiIds) || wikiIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 将 Long 类型的 wikiIds 转换为 String 类型的列表
        List<String> wikiIdStrings = wikiIds.stream()
                .map(String::valueOf)
                .toList();

        FilterExpressionBuilder filter = new FilterExpressionBuilder();
        SearchRequest build = SearchRequest.builder()
                .query(question)
                .topK(topK)
                .filterExpression(filter.in("wikiId", wikiIdStrings.toArray()).build()) // 使用 in 操作符
                .build();

        return storeService.loadMilvusVectorStore("iims", "wiki")
                .similaritySearch(build);
    }

    @Override
    public Boolean addDocumentByWiki(Long wikiId) {
        DocumentService service = factory.getService(DomProcessEnum.IIMS_INTEGRAL_ADD_DOM);
        return service.process(wikiId);
    }

    @Override
    public void delDocumentByWiki(Long wikiId) {
        FilterExpressionBuilder filter = new FilterExpressionBuilder();
        storeService.loadMilvusVectorStore("iims", "wiki")
                .delete(filter.eq("wikiId", wikiId).build());
    }

    @Override
    public void delDocumentByDocId(Long docId, DocumentTypeEnum type) {
        FilterExpressionBuilder filter = new FilterExpressionBuilder();
        storeService.loadMilvusVectorStore("iims", "wiki")
                .delete(filter.and(filter.eq("docId", docId), filter.eq("type", type.name())).build());
    }


}
