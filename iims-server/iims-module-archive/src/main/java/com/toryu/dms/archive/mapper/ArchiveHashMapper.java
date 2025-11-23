package com.toryu.dms.archive.mapper;

import com.toryu.dms.archive.model.entity.ArchiveMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Author: Aitenry
 * @Date: 2025/11/16 12:54
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface ArchiveHashMapper {

    @Select("SELECT * FROM iims_archive_mapper WHERE organization_final_id = #{id}")
    ArchiveMapper getMapperById(Long id);

}
