package com.toryu.iims.integral.mapper;

import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.integral.ArticleContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleContentMapper {

    @AutoFill(value = OperationType.INSERT)
    int insert(ArticleContent articleContent);

    @AutoFill(value = OperationType.UPDATE)
    void updateByArticleId(ArticleContent articleContent);

    @Select("select * from iims_integral_article_content where article_id = #{articleId}")
    ArticleContent selectByArticleId(Long articleId);
}
