package com.toryu.iims.integral.model.vo.wiki;

import com.toryu.iims.integral.model.vo.article.FindPreNextArticleVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindWikiArticlePreNextVO {
    /**
     * 上一篇文章
     */
    private FindPreNextArticleVO preArticle;
    /**
     * 下一篇文章
     */
    private FindPreNextArticleVO nextArticle;

}