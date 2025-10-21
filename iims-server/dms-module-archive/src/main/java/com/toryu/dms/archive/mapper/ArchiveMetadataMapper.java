package com.toryu.dms.archive.mapper;

import com.github.pagehelper.Page;
import com.toryu.dms.archive.model.dto.ArchiveMenuPageQueryDTO;
import com.toryu.dms.archive.model.entity.ArchiveBaseMetadata;
import com.toryu.dms.archive.model.entity.ArchiveMetadata;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.dms.archive.model.vo.ArchiveMetadataVO;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArchiveMetadataMapper {

    Page<ArchiveBaseMetadata> pageQuery(ArchiveMenuPageQueryDTO archiveMenuPageQueryDto);

    @Select("SELECT * FROM dms_archive_metadata WHERE id = #{id}")
    ArchiveMetadataVO getArchiveMetadataById(Long id);

    /**
     *根据主键动态修改属性
     * @param metadata ArchiveMetadata
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(ArchiveMetadata metadata);

    /**
     *添加档案属性
     * @param metadata ArchiveMetadata
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(ArchiveMetadata metadata);

    /**
     * 批量更新档案删除状态
     * @param deletedStatus ArchiveStatus
     */
    @AutoFill(value = OperationType.UPDATE)
    void updateArchiveDeleted(DeletedStatus deletedStatus);
}
