package com.toryu.iims.integral.model.vo.wiki;

import com.toryu.iims.integral.model.vo.article.FindPreNextArticleVO;
import com.toryu.iims.common.model.entity.integral.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiArticleDetailVO {

    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章正文（HTML）
     */
    private String content;
    /**
     * 发布时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 分类 ID
     */
    private Long categoryId;
    /**
     * 分类名称
     */
    private String categoryName;
    /**
     * 阅读量
     */
    private Long readNum;
    /**
     * 标签集合
     */
    private List<Tag> tags;
    /**
     * 上一篇文章
     */
    private FindPreNextArticleVO preArticle;
    /**
     * 下一篇文章
     */
    private FindPreNextArticleVO nextArticle;

    /**
     * 总字数
     */
    private Integer totalWords;

    /**
     * 阅读时长
     */
    private String readTime;

}
