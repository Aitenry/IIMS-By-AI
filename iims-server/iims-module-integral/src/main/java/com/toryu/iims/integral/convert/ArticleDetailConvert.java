package com.toryu.iims.integral.convert;

import com.toryu.iims.integral.model.vo.article.FindArticleDetailVO;
import com.toryu.iims.common.model.entity.integral.Article;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @description: 文章详情转换
 **/
@Mapper
public interface ArticleDetailConvert {
    /**
     * 初始化 convert 实例
     */
    ArticleDetailConvert INSTANCE = Mappers.getMapper(ArticleDetailConvert.class);

    /**
     * 将 DO 转化为 VO
     */
    FindArticleDetailVO convertDO2VO(Article bean);

}
