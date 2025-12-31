package com.toryu.iims.integral.service;

import com.toryu.iims.common.model.entity.integral.Article;
import com.toryu.iims.common.model.entity.integral.ArticleContent;
import com.toryu.iims.common.model.entity.integral.DictValue;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.model.dto.article.FindArticleDetailDTO;
import com.toryu.iims.integral.model.dto.article.FindArticlePageListDTO;
import com.toryu.iims.integral.model.dto.article.PublishArticleDTO;
import com.toryu.iims.integral.model.dto.article.UpdateArticleDTO;
import com.toryu.iims.integral.model.vo.article.FindArticleDetailVO;
import com.toryu.iims.integral.model.vo.article.FindArticleInfoDetailVO;

import java.util.List;

public interface ArticleService {
    /**
     * 发布文章
     */
    void publishArticle(PublishArticleDTO publishArticleDto);

    /**
     * 删除文章
     */
    void deleteArticle(List<Long> ids);

    void updateChunkKeys(Long id, List<String> chunkKeys);

    /**
     * 查询文章分页数据
     */
    PageResult findArticlePageList(FindArticlePageListDTO findArticlePageListDto);

    /**
     * 查询文章详情
     */
    FindArticleDetailVO findArticleDetail(Long articleId);

    Article selectById(Long articleId);

    ArticleContent selectByArticleId(Long articleId);

    List<DictValue> selectTagsByArticleId(Long articleId);

    DictValue selectCategoryByArticleId(Long articleId);

    /**
     * 更新文章
     */
    void updateArticle(UpdateArticleDTO updateArticleDto);

    /**
     * 更新文章是否置顶
     */
    void updateArticleIsTop(Boolean isTop, Long id);

    void updateTypeByIds(Integer type, List<Long> articleIds);

    Article selectNextArticle(Long articleId);

    Article selectPreArticle(Long articleId);

    void buildArticleByFileId(Long fileId);

    FindArticleInfoDetailVO findArticleInfoDetail(FindArticleDetailDTO dto);
}
