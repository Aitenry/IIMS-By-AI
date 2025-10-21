package com.toryu.dms.archive.mapper;

import com.toryu.dms.archive.model.vo.ArchiveMenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArchiveMenuMapper {

    @Select("SELECT m.id, m.parent_id, m.icon, m.label, m.label_code, t.name AS type_label, t.operate_component, t.detail_component " +
            "FROM dms_archive_menu m LEFT JOIN dms_archive_type t ON m.type_id = t.id WHERE m.status = 1;")
    List<ArchiveMenuVO> allMenu();

}
