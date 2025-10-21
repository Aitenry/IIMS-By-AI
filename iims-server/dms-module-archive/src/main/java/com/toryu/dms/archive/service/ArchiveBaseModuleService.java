package com.toryu.dms.archive.service;

import com.toryu.dms.archive.model.dto.AddArchiveMetadataDTO;
import com.toryu.dms.archive.model.dto.ArchiveMenuPageQueryDTO;
import com.toryu.dms.archive.model.dto.EditArchiveMetadataDTO;
import com.toryu.dms.archive.model.vo.ArchiveMenuVO;
import com.toryu.dms.archive.model.vo.ArchiveMetadataVO;
import com.toryu.iims.common.result.PageResult;

import java.util.List;

public interface ArchiveBaseModuleService {

    List<ArchiveMenuVO> getTreeMenus(Long id);

    PageResult pageQueryBaseMetadata(ArchiveMenuPageQueryDTO archiveMenuPageQueryDto);

    ArchiveMetadataVO getArchiveMetadataById(Long id);

    void editArchiveMetadata(EditArchiveMetadataDTO editArchiveMetadataDto);

    void addArchiveMetadata(AddArchiveMetadataDTO addArchiveMetadataDto);

    void delArchiveMetadata(List<Long> ids);
}
