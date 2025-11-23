package com.toryu.iims.integral.factory;

import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import com.toryu.iims.ai.rag.factory.DocumentService;
import com.toryu.iims.ai.rag.service.CustomizeVectorStoreService;
import com.toryu.iims.ai.rag.utils.MarkdownSplitter;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.model.entity.integral.WikiCatalog;
import com.toryu.iims.integral.model.vo.article.FindArticleDetailVO;
import com.toryu.iims.integral.service.ArticleService;
import com.toryu.iims.integral.service.WikiService;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class WikiDocumentFactory implements DocumentService {

    private final ArticleService articleService;
    private final WikiService wikiService;
    private final CustomizeVectorStoreService storeService;

    private WikiDocumentFactory(ArticleService articleService, WikiService wikiService,
                                CustomizeVectorStoreService storeService) {
        this.articleService = articleService;
        this.wikiService = wikiService;
        this.storeService = storeService;
    }

    @Override
    public DomProcessEnum getType() {
        return DomProcessEnum.IIMS_INTEGRAL_ADD_DOM;
    }

    @Override
    public Boolean process(Long wikiId) {

        List<WikiCatalog> wikiCatalogs = wikiService.findWikiById(wikiId);
        List<Long> ids = new ArrayList<>();
        for (WikiCatalog wikiCatalog: wikiCatalogs) {
            Long docId = wikiCatalog.getDocId();
            if (wikiCatalog.getIsEmbedding() || Objects.isNull(docId)) {
                continue;
            }
            DocumentTypeEnum type = wikiCatalog.getType();
            String wikiIdStr = String.valueOf(wikiId);
            String docIdStr = String.valueOf(docId);

            FindArticleDetailVO articleDetail = articleService.findArticleDetail(docId);
            String content = articleDetail.getContent();
            List<Document> documents = MarkdownSplitter.splitByHeadersWithHierarchy(
                    content, Map.of("wikiId", wikiIdStr, "docId", docIdStr, "type", type));
            if (!documents.isEmpty()) {
                storeService.loadMilvusVectorStore("iims", "wiki")
                        .add(documents);
                ids.add(wikiCatalog.getId());
            }
        }
        if (!ids.isEmpty()) {
            wikiService.updateIsEmbedding(ids);
        }
        return true;
    }
}
