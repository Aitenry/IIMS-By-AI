package com.toryu.iims.integral.factory;

import com.toryu.iims.integral.model.vo.article.FindArticleDetailVO;
import com.toryu.iims.integral.service.ArticleService;
import com.toryu.iims.ai.rag.factory.DocTypeService;
import com.toryu.iims.ai.rag.utils.MarkdownSplitter;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.model.entity.integral.Article;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ArticleDocTypeFactory implements DocTypeService {

    private final ArticleService articleService;

    public ArticleDocTypeFactory(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public DocumentTypeEnum getType() {
        return DocumentTypeEnum.ARTICLE;
    }

    @Override
    public List<Document> getDocument(Long id) {
        FindArticleDetailVO articleDetail = articleService.findArticleDetail(id);
        return MarkdownSplitter.splitByHeadersWithHierarchy(articleDetail.getContent(), new HashMap<>());
    }

    @Override
    public String getName(Long id) {
        Article article = articleService.selectById(id);
        return article.getTitle();
    }
}
