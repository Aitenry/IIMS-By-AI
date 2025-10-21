package com.toryu.iims.integral.mapper;

import com.toryu.iims.integral.model.entity.DeletedWikiStatus;
import com.toryu.iims.integral.model.entity.EmbeddingCount;
import com.toryu.iims.integral.model.vo.wiki.WikiCatalogVO;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.DocumentTypeEnum;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.integral.WikiCatalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface WikiCatalogMapper {

    /**
     * 新增知识库目录
     *
     * @param wikiCatalog WikiCatalog
     */
    @AutoFill(value = OperationType.INSERT)
    int insert(WikiCatalog wikiCatalog);

    @Select("select * from iims_integral_wiki_catalog where wiki_id = #{wikiId} and is_deleted = 0")
    List<WikiCatalog> selectByWikiId(Long wikiId);

    @Select("select COUNT(CASE WHEN is_embedding = 1 THEN 1 END) AS count, " +
            "COUNT(*) AS total from iims_integral_wiki_catalog " +
            "where wiki_id = #{wikiId} and level = 2 and is_deleted = 0 " +
            "and doc_id is not null " +
            "and is_embedding is not null")
    EmbeddingCount countEmbedding(Long wikiId);

    @AutoFill(value = OperationType.UPDATE)
    void deleteByWikiId(DeletedWikiStatus deletedWikiStatus);

    WikiCatalog selectFirstArticleId(Long wikiId);

    @AutoFill(value = OperationType.INSERT)
    void insertBatchSomeColumn(List<WikiCatalog> wikiCatalogList);

    @Select("""
            SELECT *
            FROM iims_integral_wiki_catalog
            WHERE wiki_id = #{wikiId}
              AND doc_id = #{docId}
            LIMIT 1;""")
    WikiCatalogVO selectByWikiIdAndArticleId(Long wikiId, Long docId);

    @Select("""
            SELECT *
            FROM iims_integral_wiki_catalog
            WHERE wiki_id = #{wikiId}
              AND doc_id IS NOT NULL
              AND id > #{catalogId}
            ORDER BY id ASC
            LIMIT 1;""")
    WikiCatalogVO selectPreArticle(Long wikiId, Long catalogId);

    @Select("""
            SELECT *
            FROM iims_integral_wiki_catalog
            WHERE wiki_id = #{wikiId}
              AND doc_id IS NOT NULL
              AND id < #{catalogId}
            ORDER BY id DESC
            LIMIT 1;""")
    WikiCatalogVO selectNextArticle(Long wikiId, Long catalogId);

    @Update({
            "<script>",
            "UPDATE iims_integral_wiki_catalog",
            "SET is_embedding = #{isEmbedding}",
            "WHERE id IN",
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
            "#{id}",
            "</foreach>",
            "</script>"
    })
    void updateIsEmbedding(List<Long> ids, Boolean isEmbedding);


    @Select("SELECT * FROM iims_integral_wiki_catalog WHERE doc_id = #{docId} AND type = #{type} AND is_deleted = 0")
    WikiCatalog selectWikiByDoc(Long docId, DocumentTypeEnum type);

}
