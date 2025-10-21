package com.toryu.iims.integral.controller;

import com.toryu.iims.integral.model.dto.article.FindArticlePageListDTO;
import com.toryu.iims.integral.model.dto.article.PublishArticleDTO;
import com.toryu.iims.integral.model.dto.article.UpdateArticleDTO;
import com.toryu.iims.integral.model.vo.article.FindArticleDetailVO;
import com.toryu.iims.integral.service.ArticleService;
import com.toryu.iims.ai.rag.model.entity.DocumentType;
import com.toryu.iims.ai.chat.service.ModelToolService;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/iims/article")
@Api(tags = "文章")
@Slf4j
public class ArticleController {
    
    private final ArticleService articleService;
    private final ModelToolService modelToolService;

    public ArticleController(ArticleService articleService, ModelToolService modelToolService) {
        this.articleService = articleService;
        this.modelToolService = modelToolService;
    }

    @PostMapping("/publish")
    @ApiOperation(value = "文章发布")
    public Result<String> publishArticle(@RequestBody @Validated PublishArticleDTO publishArticleDto) {
        articleService.publishArticle(publishArticleDto);
        return Result.success();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "文章删除")
    public Result<String> deleteArticle(@RequestBody List<Long> ids) {
        articleService.deleteArticle(ids);
        return Result.success();
    }

    @PostMapping("/page")
    @ApiOperation(value = "查询文章分页数据")
    public Result<PageResult> findArticlePageList(@RequestBody @Validated FindArticlePageListDTO findArticlePageListDto) {
        PageResult articlePageList = articleService.findArticlePageList(findArticlePageListDto);
        return Result.success(articlePageList);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "查询文章详情")
    public Result<FindArticleDetailVO> findArticleDetail(@PathVariable Long id) {
        FindArticleDetailVO articleDetail = articleService.findArticleDetail(id);
        return Result.success(articleDetail);
    }

    @PutMapping("/update")
    @ApiOperation(value = "更新文章")
    public Result<String> updateArticle(@RequestBody @Validated UpdateArticleDTO updateArticleDto) {
        articleService.updateArticle(updateArticleDto);
        return Result.success();
    }

    @PostMapping("/isTop/update/{isTop}")
    @ApiOperation(value = "更新文章置顶状态")
    public Result<String> updateArticleIsTop(@PathVariable Boolean isTop, @RequestParam("id") Long id) {
        articleService.updateArticleIsTop(isTop, id);
        return Result.success();
    }

    @GetMapping("/build/{fileId}")
    @ApiOperation(value = "根据文件来构建文章")
    public Result<String> buildArticleByFileId(@PathVariable Long fileId) {
        articleService.buildArticleByFileId(fileId);
        return Result.success();
    }

    /**
     * （文章/文档）生成摘要
     * @param id 对象ID
     * @return 生成的摘要
     */
    @ApiOperation("（文章/文档）生成摘要")
    @PostMapping("/generate/summary/{id}")
    public Result<String> generateSummary(@PathVariable Long id) {
        DocumentType build = DocumentType.builder().id(id).type(DocumentTypeEnum.ARTICLE).build();
        return Result.success(modelToolService.generateSummaryByModel(build));
    }

}
