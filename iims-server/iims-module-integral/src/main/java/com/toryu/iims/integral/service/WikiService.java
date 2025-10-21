package com.toryu.iims.integral.service;

import com.toryu.iims.integral.model.dto.article.FindArticleDetailDTO;
import com.toryu.iims.integral.model.dto.wiki.*;
import com.toryu.iims.integral.model.vo.wiki.FindWikiArticleDetailVO;
import com.toryu.iims.integral.model.vo.wiki.FindWikiArticlePreNextVO;
import com.toryu.iims.integral.model.vo.wiki.FindWikiCatalogListVO;
import com.toryu.iims.common.model.entity.integral.WikiCatalog;
import com.toryu.iims.common.result.PageResult;

import java.util.List;

public interface WikiService {
    /**
     * 新增知识库
     */
    void addWiki(AddWikiDTO addWikiDto);

    /**
     * 删除知识库
     */
    void deleteWiki(DeleteWikiDTO deleteWikiDto);

    /**
     * 知识库分页查询
     */
    PageResult findWikiPageList(FindWikiPageListDTO dto, Boolean openPublish);

    default PageResult findWikiPublishPageList(FindWikiPageListDTO dto) {
        return findWikiPageList(dto, true);
    }

    /**
     * 更新知识库置顶状态
     */
    void updateWikiIsTop(UpdateWikiIsTopDTO updateWikiIsTopDto);

    /**
     * 更新知识库发布状态
     */
    void updateWikiIsPublish(UpdateWikiIsPublishDTO updateWikiIsPublishDto);

    /**
     * 更新知识库
     */
    void updateWiki(UpdateWikiDTO updateWikiDto);

    /**
     * 查询知识库目录
     */
    List<FindWikiCatalogListVO> findWikiCatalogList(FindWikiCatalogListDTO findWikiCatalogListDto);

    /**
     * 更新知识库目录
     */
    void updateWikiCatalogs(UpdateWikiCatalogDTO updateWikiCatalogDto);

    List<WikiCatalog> findWikiById(Long wikiId);

    void updateIsEmbedding(List<Long> ids);

    FindWikiArticlePreNextVO findArticlePreNext(FindWikiArticlePreNextDTO articlePreNextDto);

    FindWikiArticleDetailVO findArticleDetail(FindArticleDetailDTO articleDetailDto);
}
