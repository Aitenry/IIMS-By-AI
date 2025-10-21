package com.toryu.iims.integral.controller;

import com.toryu.iims.integral.model.dto.article.FindArticleDetailDTO;
import com.toryu.iims.integral.model.dto.wiki.*;
import com.toryu.iims.integral.model.vo.wiki.FindWikiArticleDetailVO;
import com.toryu.iims.integral.model.vo.wiki.FindWikiArticlePreNextVO;
import com.toryu.iims.integral.model.vo.wiki.FindWikiCatalogListVO;
import com.toryu.iims.integral.service.WikiService;
import com.toryu.iims.ai.rag.enums.DomProcessEnum;
import com.toryu.iims.ai.chat.service.ModelToolService;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/iims/wiki")
@Api(tags = "Admin 知识库模块")
public class WikiController {

    private final WikiService wikiService;
    private final ModelToolService modelToolService;

    public WikiController(WikiService wikiService, ModelToolService modelToolService) {
        this.wikiService = wikiService;
        this.modelToolService = modelToolService;
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增知识库")
    public Result<String> addWiki(@RequestBody @Validated AddWikiDTO addWikiDto) {
        wikiService.addWiki(addWikiDto);
        return Result.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "知识库删除")
    public Result<String> deleteWiki(@RequestBody @Validated DeleteWikiDTO deleteWikiDto) {
        wikiService.deleteWiki(deleteWikiDto);
        return Result.success();
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询知识库分页数据")
    public Result<PageResult> findWikiPageList(@RequestBody @Validated FindWikiPageListDTO dto) {
        return Result.success(wikiService.findWikiPageList(dto, false));
    }

    @PostMapping("/publish/list")
    @ApiOperation(value = "查询知识库分页数据")
    public Result<PageResult> findPublishWikiPageList(@RequestBody @Validated FindWikiPageListDTO dto) {
        return Result.success(wikiService.findWikiPublishPageList(dto));
    }

    @PostMapping("/isTop/update")
    @ApiOperation(value = "更新知识库置顶状态")
    public Result<String> updateWikiIsTop(@RequestBody @Validated UpdateWikiIsTopDTO updateWikiIsTopDto) {
        wikiService.updateWikiIsTop(updateWikiIsTopDto);
        return Result.success();
    }

    @PostMapping("/isPublish/update")
    @ApiOperation(value = "更新知识库发布状态")
    public Result<String> updateWikiIsPublish(@RequestBody @Validated UpdateWikiIsPublishDTO updateWikiIsPublishDto) {
        wikiService.updateWikiIsPublish(updateWikiIsPublishDto);
        return Result.success();
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新知识库")
    public Result<String> updateWiki(@RequestBody @Validated UpdateWikiDTO updateWikiDto) {
        wikiService.updateWiki(updateWikiDto);
        return Result.success();
    }

    @PostMapping("/catalog/list")
    @ApiOperation(value = "查询知识库目录数据")
    public Result<List<FindWikiCatalogListVO>> findWikiCatalogList(@RequestBody @Validated FindWikiCatalogListDTO findWikiCatalogListDto) {
        return Result.success(wikiService.findWikiCatalogList(findWikiCatalogListDto));
    }

    @PostMapping("/catalog/update")
    @ApiOperation(value = "更新知识库目录")
    public Result<String> updateWikiCatalogs(@RequestBody @Valid UpdateWikiCatalogDTO updateWikiCatalogDto) {
        wikiService.updateWikiCatalogs(updateWikiCatalogDto);
        return Result.success();
    }

    @PostMapping("/article/preNext")
    @ApiOperation(value = "获取知识库文章上下页")
    public Result<FindWikiArticlePreNextVO> findArticlePreNext(@RequestBody FindWikiArticlePreNextDTO articlePreNextDto) {
        return Result.success(wikiService.findArticlePreNext(articlePreNextDto));
    }

    @PostMapping("/article/detail")
    @ApiOperation(value = "获取知识库文章详情")
    public Result<FindWikiArticleDetailVO> findArticleDetail(@RequestBody FindArticleDetailDTO articleDetailDto) {
        return Result.success(wikiService.findArticleDetail(articleDetailDto));
    }


    @ApiOperation("（文章/文档）向量化")
    @PostMapping("/document/embedding/{wikiId}")
    public Result<String> documentEmbedding(@PathVariable Long wikiId) {
        modelToolService.documentEmbeddingByModel(wikiId, DomProcessEnum.IIMS_INTEGRAL_ADD_DOM);
        return Result.success();
    }

}
