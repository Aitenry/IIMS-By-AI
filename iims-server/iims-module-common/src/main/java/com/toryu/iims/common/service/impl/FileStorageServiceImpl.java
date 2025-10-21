package com.toryu.iims.common.service.impl;

import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.enums.FileStatusEnum;
import com.toryu.iims.common.mapper.FileStorageMapper;
import com.toryu.iims.common.model.entity.file.FileStatus;
import com.toryu.iims.common.model.entity.file.FileWarehouse;
import com.toryu.iims.common.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageMapper fileStorageMapper;

    public FileStorageServiceImpl(FileStorageMapper fileStorageMapper) {
        this.fileStorageMapper = fileStorageMapper;
    }

    @Override
    public List<FileWarehouse> getObjectByIds(List<Long> ids) {
        return fileStorageMapper.getObjectByIds(BaseContext.getCurrentId(), ids);
    }

    @Override
    public FileWarehouse getObjectById(Long id) {
        return fileStorageMapper.getObjectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(FileWarehouse fileWarehouse) {
        fileStorageMapper.update(fileWarehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeFile(
            Long oldFileId, Integer changOldStatus,
            Long newFileId, Integer changNewStatus) {
        if (!Objects.equals(oldFileId, newFileId)) {
            fileStorageMapper.updateFileStatus(FileStatus.builder()
                    .id(oldFileId).fileStatus(changOldStatus).build());
            fileStorageMapper.updateFileStatus(FileStatus.builder()
                    .id(newFileId).fileStatus(changNewStatus).build());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFileStatus(Long fileId, FileStatusEnum status) {
        fileStorageMapper.updateFileStatus(FileStatus.builder()
                .id(fileId).fileStatus(status.getCode()).build());
    }
}
