package com.toryu.iims.common.service;

import com.toryu.iims.common.enums.FileStatusEnum;
import com.toryu.iims.common.model.entity.file.FileWarehouse;

import java.util.List;

public interface FileStorageService {

    List<FileWarehouse> getObjectByIds(List<Long> ids);

    FileWarehouse getObjectById(Long id);

    void update(FileWarehouse fileWarehouse);

    void changeFile(Long oldFileId, Integer changOldStatus,
                    Long newFileId, Integer changNewStatus);

    void updateFileStatus(Long fileId, FileStatusEnum status);

}
