package com.toryu.iims.integral.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.integral.model.vo.article.FindArticlePageListVO;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.integral.Article;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.model.entity.status.TypeStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface ArticleMapper {

    @AutoFill(value = OperationType.INSERT)
    int insert(Article article);

    @AutoFill(value = OperationType.UPDATE)
    int updateById(Article article);

    @AutoFill(value = OperationType.UPDATE)
    void updateTypeByIds(TypeStatus typeStatus);

    Page<FindArticlePageListVO> pageQuery(String title, LocalDateTime startDate, LocalDateTime endDate, Integer type);

    @Select("select weight from iims_integral_article order by weight desc limit 1")
    int selectMaxWeight();

    @Select("select * from iims_integral_article where id = #{articleId}")
    Article selectById(Long articleId);

    void updateArticleDeleted(DeletedStatus deletedStatus);

    @Select("""
            SELECT *
            FROM iims_integral_article
            WHERE id > #{articleId}
            ORDER BY id ASC
            LIMIT 1;""")
    Article selectNextArticle(Long articleId);

    @Select("""
            SELECT *
            FROM iims_integral_article
            WHERE id < #{articleId}
            ORDER BY id DESC
            LIMIT 1;""")
    Article selectPreArticle(Long articleId);

    @Update("""
            UPDATE iims_integral_article
            SET read_num = read_num + 1
            WHERE id = #{articleId};""")
    void increaseReadNum(Long articleId);
}
