package com.toryu.iims.integral.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.toryu.iims.common.enums.ArticleTypeEnum;
import com.toryu.iims.common.enums.ResponseCodeEnum;
import com.toryu.iims.common.enums.TaskStatusEnum;
import com.toryu.iims.common.enums.WikiCatalogLevelEnum;
import com.toryu.iims.common.exception.BizException;
import com.toryu.iims.common.markdown.MarkdownHelper;
import com.toryu.iims.common.model.entity.integral.*;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.service.MinioService;
import com.toryu.iims.common.utils.MarkdownStatsUtil;
import com.toryu.iims.integral.convert.WikiConvert;
import com.toryu.iims.integral.event.ReadArticleEvent;
import com.toryu.iims.integral.mapper.WikiCatalogMapper;
import com.toryu.iims.integral.mapper.WikiMapper;
import com.toryu.iims.integral.model.dto.article.FindArticleDetailDTO;
import com.toryu.iims.integral.model.dto.wiki.*;
import com.toryu.iims.integral.model.entity.DeletedWikiStatus;
import com.toryu.iims.integral.model.entity.EmbeddingCount;
import com.toryu.iims.integral.model.vo.article.FindPreNextArticleVO;
import com.toryu.iims.integral.model.vo.wiki.*;
import com.toryu.iims.integral.service.ArticleService;
import com.toryu.iims.integral.service.WikiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WikiServiceImpl implements WikiService {

    private final WikiMapper wikiMapper;

    private final WikiCatalogMapper wikiCatalogMapper;

    private final ArticleService articleService;

    private final MinioService minioService;

    private final ApplicationEventPublisher eventPublisher;

    public WikiServiceImpl(WikiMapper wikiMapper, WikiCatalogMapper wikiCatalogMapper,
                            ArticleService articleService, MinioService minioService, ApplicationEventPublisher eventPublisher) {
        this.wikiMapper = wikiMapper;
        this.wikiCatalogMapper = wikiCatalogMapper;
        this.articleService = articleService;
        this.minioService = minioService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWiki(AddWikiDTO addWikiDto) {
        Wiki wiki = Wiki.builder()
                .cover(addWikiDto.getCover())
                .title(addWikiDto.getTitle())
                .summary(addWikiDto.getSummary())
                .type(addWikiDto.getType())
                .weight(0).isPublish(false).isDeleted(false)
                .build();
        wikiMapper.insert(wiki);
        // 获取新增记录的主键 ID
        Long wikiId = wiki.getId();

        // 初始化默认目录
        wikiCatalogMapper.insert(WikiCatalog.builder()
                .wikiId(wikiId).title("概述")
                .level(1).isDeleted(false).isEmbedding(false)
                .sort(1).build());
        wikiCatalogMapper.insert(WikiCatalog.builder()
                .wikiId(wikiId).title("基础")
                .level(1).isDeleted(false).isEmbedding(false)
                .sort(2).build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWiki(DeleteWikiDTO deleteWikiDto) {
        Long wikiId = deleteWikiDto.getId();

        // 删除知识库
        int count = wikiMapper.deleteById(DeletedStatus.builder()
                .ids(List.of(deleteWikiDto.getId())).isDeleted(true).build());

        // 若知识库不存在
        if (count == 0) {
            log.warn("该知识库不存在, wikiId: {}", wikiId);
            throw new BizException(ResponseCodeEnum.WIKI_NOT_FOUND);
        }

        // 查询此知识库下所有目录
        List<WikiCatalog> wikiCatalogDOS = wikiCatalogMapper.selectByWikiId(wikiId);
        // 过滤目录中所有文章的 ID
        List<Long> docIds = wikiCatalogDOS.stream()
                .filter(wikiCatalogDO -> Objects.nonNull(wikiCatalogDO.getDocId())  // 文章 ID 不为空
                        && Objects.equals(wikiCatalogDO.getLevel(), WikiCatalogLevelEnum.TWO.getValue())) // 二级目录
                .map(WikiCatalog::getDocId) // 提取文章 ID
                .collect(Collectors.toList());

        // 更新文章类型 type 为普通
        if (!CollectionUtils.isEmpty(docIds)) {
            articleService.updateTypeByIds(ArticleTypeEnum.NORMAL.getValue(), docIds);
        }

        // 删除知识库目录
        wikiCatalogMapper.deleteByWikiId(DeletedWikiStatus.builder()
                .wikiId(wikiId).isDeleted(1).build());
    }

    @Override
    public PageResult findWikiPageList(FindWikiPageListDTO dto, Boolean openPublish) {
        // 获取当前页、以及每页需要展示的数据数量
        int page = dto.getPage();
        int pageSize = dto.getPageSize();
        PageHelper.startPage(page, pageSize);

        // 执行分页查询
        Page<Wiki> wikiPage = wikiMapper.pageQuery(dto, openPublish);

        // 获取查询记录
        long total = wikiPage.getTotal();
        List<Wiki> records = wikiPage.getResult();

        List<FindWikiPageListVO> vos = null;
        if (!CollectionUtils.isEmpty(records)) {
            vos = records.stream()
                    .map(WikiConvert.INSTANCE::convertDO2VO)
                    .collect(Collectors.toList());
        }

        // 设置每个知识库的第一篇文章 ID，方便前端跳转
        if (Objects.nonNull(vos)) {
            vos.forEach(vo -> {
                Long wikiId = vo.getId();
                WikiCatalog wikiCatalogDO = wikiCatalogMapper.selectFirstArticleId(wikiId);
                EmbeddingCount embeddingCounts = wikiCatalogMapper.countEmbedding(wikiId);
                Long embeddingCount = embeddingCounts.getCount();
                Long embeddingTotal = embeddingCounts.getTotal();
                vo.setTaskStatus(TaskStatusEnum.fromCounts(embeddingCount, embeddingTotal));
                vo.setImgUrl(minioService.generateShortLink(vo.getCover()));
                vo.setFirstArticleId(Objects.nonNull(wikiCatalogDO) ? wikiCatalogDO.getDocId() : null);
            });
        }
        return new PageResult(total, vos);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWikiIsTop(UpdateWikiIsTopDTO updateWikiIsTopDto) {
        Long wikiId = updateWikiIsTopDto.getId();
        Boolean isTop = updateWikiIsTopDto.getIsTop();

        // 默认权重值为 0 ，即不参与置顶
        int weight = 0;
        // 若设置为置顶
        if (isTop) {
            // 查询最大权重值
            Wiki wiki = wikiMapper.selectMaxWeight();
            Integer maxWeight = wiki.getWeight();
            // 最大权重值加一
            weight = maxWeight + 1;
        }

        // 更新该知识库的权重值
        wikiMapper.updateById(Wiki.builder().id(wikiId).weight(weight).build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWikiIsPublish(UpdateWikiIsPublishDTO updateWikiIsPublishDto) {
        Long wikiId = updateWikiIsPublishDto.getId();
        Boolean isPublish = updateWikiIsPublishDto.getIsPublish();
        // 更新发布状态
        wikiMapper.updateById(Wiki.builder().id(wikiId).isPublish(isPublish).build());
    }

    @Override
    public void updateWiki(UpdateWikiDTO updateWikiDto) {
        Wiki wiki = Wiki.builder()
                .id(updateWikiDto.getId())
                .title(updateWikiDto.getTitle())
                .cover(updateWikiDto.getCover())
                .type(updateWikiDto.getType())
                .summary(updateWikiDto.getSummary())
                .build();

        // 根据 ID 更新知识库
        wikiMapper.updateById(wiki);
    }

    @Override
    public List<FindWikiCatalogListVO> findWikiCatalogList(FindWikiCatalogListDTO findWikiCatalogListDto) {
        Long wikiId = findWikiCatalogListDto.getId();

        // 查询此知识库下所有目录
        List<WikiCatalog> catalogs = wikiCatalogMapper.selectByWikiId(wikiId);

        // DO 转 VO
        // 组装一、二级目录结构
        List<FindWikiCatalogListVO> vos = null;
        if (!CollectionUtils.isEmpty(catalogs)) {
            vos = Lists.newArrayList();

            // 提取一级目录
            List<WikiCatalog> levelOCatalogs = catalogs.stream()
                    .filter(catalogDO -> Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.ONE.getValue())) // 一级目录
                    .sorted(Comparator.comparing(WikiCatalog::getSort)).toList();

            for (WikiCatalog levelOCatalog : levelOCatalogs) {
                vos.add(FindWikiCatalogListVO.builder()
                        .id(levelOCatalog.getId())
                        .docId(levelOCatalog.getDocId())
                        .type(levelOCatalog.getType())
                        .title(levelOCatalog.getTitle())
                        .level(levelOCatalog.getLevel())
                        .sort(levelOCatalog.getSort())
                        .isEmbedding(levelOCatalog.getIsEmbedding())
                        .editing(Boolean.FALSE)
                        .build());
            }

            // 设置一级目录下，二级目录的数据
            vos.forEach(levelOCatalog -> {
                Long parentId = levelOCatalog.getId();
                List<WikiCatalog> levelTCatalogDOS = catalogs.stream()
                        .filter(catalogDO -> Objects.equals(catalogDO.getParentId(), parentId)
                                && Objects.equals(catalogDO.getLevel(), WikiCatalogLevelEnum.TWO.getValue()))
                        .sorted(Comparator.comparing(WikiCatalog::getSort)).toList();

                List<FindWikiCatalogListVO> levelTCatalogs = levelTCatalogDOS.stream()
                        .map(catalogDO -> FindWikiCatalogListVO.builder()
                                .id(catalogDO.getId())
                                .docId(catalogDO.getDocId())
                                .type(catalogDO.getType())
                                .title(catalogDO.getTitle())
                                .level(catalogDO.getLevel())
                                .isEmbedding(catalogDO.getIsEmbedding())
                                .sort(catalogDO.getSort())
                                .editing(Boolean.FALSE)
                                .build())
                        .collect(Collectors.toList());
                levelOCatalog.setChildren(levelTCatalogs);
            });
        }
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWikiCatalogs(UpdateWikiCatalogDTO updateWikiCatalogDto) {
        // 知识库 ID
        Long wikiId = updateWikiCatalogDto.getId();
        // 目录
        List<UpdateWikiCatalogItemDTO> catalogs = updateWikiCatalogDto.getCatalogs();

        // 1. 先将此知识库中的所有文章类型更新为普通
        // 查出此 wiki 下所有的文章 ID
        List<WikiCatalog> wikiCatalogDOS = wikiCatalogMapper.selectByWikiId(wikiId);
        List<Long> docIds = wikiCatalogDOS.stream()
                .map(WikiCatalog::getDocId)
                .filter(Objects::nonNull).collect(Collectors.toList());

        // 更新为普通文章类型
        if (!CollectionUtils.isEmpty(docIds)) {
            articleService.updateTypeByIds(ArticleTypeEnum.NORMAL.getValue(), docIds);
        }

        // 2. 先删除所有此知识库下所有目录
        wikiCatalogMapper.deleteByWikiId(DeletedWikiStatus.builder()
                .wikiId(wikiId).isDeleted(1).build());

        // 3. 再重新插入新的目录数据
        // 若入参传入的目录不为空
        if (!CollectionUtils.isEmpty(catalogs)) {
            // 重新设置排序
            for (int i = 0; i < catalogs.size(); i++) {
                UpdateWikiCatalogItemDTO vo = catalogs.get(i);
                List<UpdateWikiCatalogItemDTO> children = vo.getChildren();
                vo.setSort(i + 1);
                if (!CollectionUtils.isEmpty(children)) {
                    for (int j = 0; j < children.size(); j++) {
                        children.get(j).setSort(j + 1);
                    }
                }
            }

            // VO 转 DO
            catalogs.forEach(catalog -> {
                // 一级目录
                WikiCatalog wikiCatalogDO = WikiCatalog.builder()
                        .wikiId(wikiId)
                        .title(catalog.getTitle())
                        .level(WikiCatalogLevelEnum.ONE.getValue())
                        .sort(catalog.getSort())
                        .isEmbedding(catalog.getIsEmbedding())
                        .isDeleted(Boolean.FALSE)
                        .build();
                // 添加一级目录
                wikiCatalogMapper.insert(wikiCatalogDO);

                Long catalogId = wikiCatalogDO.getId();

                // 二级目录
                List<UpdateWikiCatalogItemDTO> children = catalog.getChildren();
                List<Long> updateDocIds = Lists.newArrayList();
                if (!CollectionUtils.isEmpty(children)) {
                    List<WikiCatalog> level2Catalogs = Lists.newArrayList();
                    children.forEach(child -> {
                        level2Catalogs.add(WikiCatalog.builder()
                                .wikiId(wikiId)
                                .title(child.getTitle())
                                .level(WikiCatalogLevelEnum.TWO.getValue())
                                .sort(child.getSort())
                                .type(child.getType())
                                .docId(child.getDocId())
                                .isEmbedding(child.getIsEmbedding())
                                .parentId(catalogId)
                                .isDeleted(Boolean.FALSE)
                                .build());

                        updateDocIds.add(child.getDocId());
                    });

                    // 批量插入
                    wikiCatalogMapper.insertBatchSomeColumn(level2Catalogs);
                    // 更新相关文章的 type 字段，知识库类型
                    articleService.updateTypeByIds(ArticleTypeEnum.WIKI.getValue(), updateDocIds);
                }
            });
        }
    }

    @Override
    public List<WikiCatalog> findWikiById(Long wikiId) {
        return wikiCatalogMapper.selectByWikiId(wikiId);
    }

    @Override
    public void updateIsEmbedding(List<Long> ids) {
        wikiCatalogMapper.updateIsEmbedding(ids, true);
    }

    @Override
    public FindWikiArticlePreNextVO findArticlePreNext(FindWikiArticlePreNextDTO articlePreNextDto) {
        // 知识库 ID
        Long wikiId = articlePreNextDto.getId();
        // 文章 ID
        Long articleId = articlePreNextDto.getArticleId();

        FindWikiArticlePreNextVO vo = new FindWikiArticlePreNextVO();
        // 获取当前文章所属知识库的目录
        WikiCatalogVO wikiCatalogDO = wikiCatalogMapper.selectByWikiIdAndArticleId(wikiId, articleId);

        // 构建上一篇文章 VO
        WikiCatalogVO preArticleDO = wikiCatalogMapper.selectPreArticle(wikiId, wikiCatalogDO.getId());
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleVO preArticleVO = FindPreNextArticleVO.builder()
                    .articleId(preArticleDO.getDocId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 构建下一篇文章 VO
        WikiCatalogVO nextArticleDO = wikiCatalogMapper.selectNextArticle(wikiId, wikiCatalogDO.getId());
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleVO nextArticleVO = FindPreNextArticleVO.builder()
                    .articleId(nextArticleDO.getDocId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }
        return vo;
    }

    @Override
    public FindWikiArticleDetailVO findArticleDetail(FindArticleDetailDTO articleDetailDto) {
        Long articleId = articleDetailDto.getArticleId();

        Article articleDO = articleService.selectById(articleId);

        // 判断文章是否存在
        if (Objects.isNull(articleDO)) {
            log.warn("==> 该文章不存在, articleId: {}", articleId);
            throw new BizException(ResponseCodeEnum.ARTICLE_NOT_FOUND);
        }

        // 查询正文
        ArticleContent articleContentDO = articleService.selectByArticleId(articleId);
        String content = articleContentDO.getContent();

        // 计算 md 正文字数
        int totalWords = MarkdownStatsUtil.calculateWordCount(content);

        // DO 转 VO
        FindWikiArticleDetailVO vo = FindWikiArticleDetailVO.builder()
                .title(articleDO.getTitle())
                .createTime(articleDO.getCreateTime())
                .content(MarkdownHelper.convertMarkdown2Html(content))
                .readNum(articleDO.getReadNum())
                .totalWords(totalWords)
                .readTime(MarkdownStatsUtil.calculateReadingTime(totalWords))
                .updateTime(articleDO.getUpdateTime())
                .build();

        // 查询所属分类
        DictValue dictValue = articleService.selectCategoryByArticleId(articleId);
        vo.setCategoryId(dictValue.getId());
        vo.setCategoryName(dictValue.getValue());

        // 查询标签
        List<DictValue> dictValues = articleService.selectTagsByArticleId(articleId);
        List<Tag> tags = new ArrayList<>();
        dictValues.forEach(value -> tags.add(Tag.builder()
                .id(value.getId()).name(value.getValue())
                .createTime(value.getCreateTime()).updateTime(value.getUpdateTime())
                .isDeleted(value.getIsDeleted()).build()));
        vo.setTags(tags);

        // 上一篇
        Article preArticleDO = articleService.selectPreArticle(articleId);
        if (Objects.nonNull(preArticleDO)) {
            FindPreNextArticleVO preArticleVO = FindPreNextArticleVO.builder()
                    .articleId(preArticleDO.getId())
                    .articleTitle(preArticleDO.getTitle())
                    .build();
            vo.setPreArticle(preArticleVO);
        }

        // 下一篇
        Article nextArticleDO = articleService.selectNextArticle(articleId);
        if (Objects.nonNull(nextArticleDO)) {
            FindPreNextArticleVO nextArticleVO = FindPreNextArticleVO.builder()
                    .articleId(nextArticleDO.getId())
                    .articleTitle(nextArticleDO.getTitle())
                    .build();
            vo.setNextArticle(nextArticleVO);
        }

        // 发布文章阅读事件
        eventPublisher.publishEvent(new ReadArticleEvent(this, articleId));

        return vo;
    }
}
