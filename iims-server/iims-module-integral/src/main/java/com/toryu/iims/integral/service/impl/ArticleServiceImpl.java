package com.toryu.iims.integral.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.enums.FileStatusEnum;
import com.toryu.iims.common.enums.ResponseCodeEnum;
import com.toryu.iims.common.exception.BizException;
import com.toryu.iims.common.model.entity.base.BaseAdminInfo;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import com.toryu.iims.common.model.entity.integral.Article;
import com.toryu.iims.common.model.entity.integral.ArticleContent;
import com.toryu.iims.common.model.entity.integral.DictValue;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.model.entity.status.TypeStatus;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.service.FileStorageService;
import com.toryu.iims.common.service.MinioService;
import com.toryu.iims.integral.event.WriteWikiDocEvent;
import com.toryu.iims.integral.mapper.ArticleContentMapper;
import com.toryu.iims.integral.mapper.ArticleMapper;
import com.toryu.iims.integral.model.dto.article.FindArticlePageListDTO;
import com.toryu.iims.integral.model.dto.article.PublishArticleDTO;
import com.toryu.iims.integral.model.dto.article.UpdateArticleDTO;
import com.toryu.iims.integral.model.vo.article.FindArticleDetailVO;
import com.toryu.iims.integral.model.vo.article.FindArticlePageListVO;
import com.toryu.iims.integral.service.AdminService;
import com.toryu.iims.integral.service.ArticleService;
import com.toryu.iims.integral.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    private final AdminService adminService;

    private final ArticleContentMapper articleContentMapper;

    private final MinioService minioService;

    private final DictService dictService;

    private final FileStorageService fileStorageService;

    private final ApplicationEventPublisher eventPublisher;

    public ArticleServiceImpl(ArticleMapper articleMapper, AdminService adminService, ArticleContentMapper articleContentMapper, MinioService minioService, DictService dictService, FileStorageService fileStorageService, ApplicationEventPublisher eventPublisher) {
        this.articleMapper = articleMapper;
        this.adminService = adminService;
        this.articleContentMapper = articleContentMapper;
        this.minioService = minioService;
        this.dictService = dictService;
        this.fileStorageService = fileStorageService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishArticle(PublishArticleDTO publishArticleDto) {

        Long cover = publishArticleDto.getCover();
        Boolean isTop = publishArticleDto.getIsTop();
        Article article = Article.builder()
                .title(publishArticleDto.getTitle())
                .cover(cover)
                .summary(publishArticleDto.getSummary())
                .weight(isTop ? articleMapper.selectMaxWeight() + 1 : 0)
                .readNum(0L).type(1).isDeleted(false)
                .dictCategoryId(publishArticleDto.getCategoryId())
                .dictTagIds(publishArticleDto.getTagIds())
                .build();
        articleMapper.insert(article);

        Long articleId = article.getId();

        ArticleContent articleContent = ArticleContent.builder()
                .articleId(articleId)
                .content(publishArticleDto.getContent())
                .build();
        if (articleContentMapper.insert(articleContent) > 0) {
            fileStorageService.updateFileStatus(cover, FileStatusEnum.USED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteArticle(List<Long> ids) {
        if (!ids.isEmpty()) {
            DeletedStatus deletedStatus = DeletedStatus.builder()
                    .isDeleted(true).ids(ids).build();
            articleMapper.updateArticleDeleted(deletedStatus);
        }
    }

    @Override
    public PageResult findArticlePageList(FindArticlePageListDTO findArticlePageListDto) {
        int page = findArticlePageListDto.getPage();
        int pageSize = findArticlePageListDto.getPageSize();
        PageHelper.startPage(page, pageSize);

        String title = findArticlePageListDto.getTitle();
        LocalDateTime startDate = findArticlePageListDto.getStartDate();
        LocalDateTime endDate = findArticlePageListDto.getEndDate();
        Integer type = findArticlePageListDto.getType();
        Page<FindArticlePageListVO> articlePageVos = articleMapper.pageQuery(title, startDate, endDate, type);

        long total = articlePageVos.getTotal();
        List<FindArticlePageListVO> records = articlePageVos.getResult();
        records.forEach(item -> {
            item.setImageUrl(minioService.getPreviewUrl(item.getCover()));
            item.setIsTop(item.getWeight() > 0);
            BaseAdminInfo userInfo = item.getUserInfo();
            userInfo.setUsername(adminService.getById(userInfo.getId()).getUsername());
            DictValue dictValue = dictService.getDictValueById(item.getDictCategoryId());
            if (Objects.nonNull(dictValue)) {
                item.setCategoryName(dictValue.getValue());
            }
            List<Long> dictTagIds = JSONArray.parseArray(item.getDictTagIds(), Long.class);
            if (Objects.nonNull(dictTagIds)) {
                List<String> tagsName = new ArrayList<>();
                dictTagIds.forEach(dictTagId -> {
                    DictValue dictValueTags = dictService.getDictValueById(dictTagId);
                    if (Objects.nonNull(dictValueTags)) {
                        tagsName.add(dictValueTags.getValue());
                    }
                });
                item.setTagsName(tagsName);
            }
        });

        return  new PageResult(total, records);
    }

    @Override
    public FindArticleDetailVO findArticleDetail(Long articleId) {
        Article article = articleMapper.selectById(articleId);

        if (Objects.isNull(article)) {
            log.warn("==> 查询的文章不存在，articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        ArticleContent articleContent = articleContentMapper.selectByArticleId(articleId);
        Long cover = article.getCover();
        FindArticleDetailVO detailVo = FindArticleDetailVO.builder()
                .id(article.getId()).title(article.getTitle())
                .categoryId(article.getDictCategoryId())
                .cover(cover).isTop(article.getWeight() > 0)
                .content(articleContent.getContent())
                .summary(article.getSummary()).build();
        detailVo.setImageUrl(minioService.getPreviewUrl(cover));
        detailVo.setTagIds(JSONArray.parseArray(article.getDictTagIds(), String.class));
        return detailVo;
    }

    @Override
    public Article selectById(Long articleId) {
        return articleMapper.selectById(articleId);
    }

    @Override
    public ArticleContent selectByArticleId(Long articleId) {
        return articleContentMapper.selectByArticleId(articleId);
    }

    @Override
    public DictValue selectCategoryByArticleId(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        return dictService.getDictValueById(article.getDictCategoryId());
    }

    @Override
    public List<DictValue> selectTagsByArticleId(Long articleId) {
        Article article = articleMapper.selectById(articleId);
        List<Long> dictTagIds = JSONArray.parseArray(article.getDictTagIds(), Long.class);
        return dictService.getDictValueByIds(dictTagIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticle(UpdateArticleDTO updateArticleDto) {
        Long articleId = updateArticleDto.getId();

        // 1. VO 转 Article, 并更新
        Boolean isTop = updateArticleDto.getIsTop();
        Article article = Article.builder()
                .id(articleId)
                .title(updateArticleDto.getTitle())
                .cover(updateArticleDto.getCover())
                .summary(updateArticleDto.getSummary())
                .weight(isTop ? articleMapper.selectMaxWeight() + 1 : 0)
                .dictCategoryId(updateArticleDto.getCategoryId())
                .dictTagIds(updateArticleDto.getTagIds())
                .build();
        int count = articleMapper.updateById(article);

        // 根据更新是否成功，来判断该文章是否存在
        if (count == 0) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 2. VO 转 ArticleContent，并更新
        String content = updateArticleDto.getContent();
        if (Objects.nonNull(content)) {
            ArticleContent articleContent = ArticleContent.builder()
                    .articleId(articleId)
                    .content(content)
                    .build();
            articleContentMapper.updateByArticleId(articleContent);
            eventPublisher.publishEvent(new WriteWikiDocEvent(this, articleId, DocumentTypeEnum.ARTICLE));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateArticleIsTop(Boolean isTop, Long id) {
        // 默认权重为 0
        int weight = 0;
        // 若设置为置顶
        if (isTop) {
            // 查询出表中最大的权重值, 最大权重值加一
            weight = articleMapper.selectMaxWeight() + 1;
        }

        // 更新该篇文章的权重值
        articleMapper.updateById(Article.builder()
                .id(id)
                .weight(weight)
                .build());

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTypeByIds(Integer type, List<Long> articleIds) {
        if (!articleIds.isEmpty()) {
            TypeStatus builder = TypeStatus.builder()
                    .type(type).ids(articleIds.toArray(new Long[0])).build();
            articleMapper.updateTypeByIds(builder);
        }
    }

    @Override
    public Article selectNextArticle(Long articleId) {
        return articleMapper.selectNextArticle(articleId);
    }

    @Override
    public Article selectPreArticle(Long articleId) {
        return articleMapper.selectPreArticle(articleId);
    }

    @Override
    public void buildArticleByFileId(Long fileId) {
        FileWarehouse fileWarehouse = fileStorageService.getObjectById(fileId);
        String name = FilenameUtils.removeExtension(fileWarehouse.getFileName());
        Article article = Article.builder()
                .title(name).weight(0)
                .readNum(0L).type(1).isDeleted(false)
                .build();
        articleMapper.insert(article);

        Long articleId = article.getId();

        ArticleContent articleContent = ArticleContent.builder()
                .articleId(articleId)
                .content("")
                .build();
        if (articleContentMapper.insert(articleContent) > 0) {
            fileStorageService.updateFileStatus(fileId, FileStatusEnum.USED);
        }
    }

}
