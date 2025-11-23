package com.toryu.dms.archive.mapper;

import com.toryu.dms.archive.model.entity.ArchiveType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/16 13:57
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Mapper
public interface ArchiveTypeMapper {

    List<ArchiveType> getArchiveTypesByIds(@Param("ids") List<Long> ids);

}
