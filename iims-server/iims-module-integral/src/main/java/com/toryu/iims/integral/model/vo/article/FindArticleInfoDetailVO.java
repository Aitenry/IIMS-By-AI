package com.toryu.iims.integral.model.vo.article;

import com.toryu.iims.common.model.entity.base.BaseTable;
import com.toryu.iims.common.model.entity.integral.Tag;
import lombok.*;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/10/25 12:00
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class FindArticleInfoDetailVO extends BaseTable {
    /**
     * 文章标题
     */
    private String title;
    /**
     * 文章正文（HTML）
     */
    private String content;

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
