package com.toryu.dms.archive.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.dms.archive.enums.ArchivalLevelEnum;
import com.toryu.dms.archive.enums.ArchiveDeadlineEnum;
import com.toryu.dms.archive.mapper.ArchiveMenuMapper;
import com.toryu.dms.archive.mapper.ArchiveMetadataMapper;
import com.toryu.dms.archive.model.dto.AddArchiveMetadataDTO;
import com.toryu.dms.archive.model.dto.ArchiveMenuPageQueryDTO;
import com.toryu.dms.archive.model.dto.EditArchiveMetadataDTO;
import com.toryu.dms.archive.model.entity.ArchiveBaseMetadata;
import com.toryu.dms.archive.model.entity.ArchiveMetadata;
import com.toryu.dms.archive.model.vo.ArchiveBaseMetadataVO;
import com.toryu.dms.archive.model.vo.ArchiveMenuVO;
import com.toryu.dms.archive.model.vo.ArchiveMetadataVO;
import com.toryu.dms.archive.service.ArchiveBaseModuleService;
import com.toryu.iims.common.model.entity.base.BaseAdminInfo;
import com.toryu.iims.common.model.entity.status.DeletedStatus;
import com.toryu.iims.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 档案基础模块层
 */
@Service
@Slf4j
public class ArchiveBaseModuleServiceImpl implements ArchiveBaseModuleService {

    private final ArchiveMenuMapper archiveMenuMapper;
    private final ArchiveMetadataMapper archiveMetadataMapper;

    public ArchiveBaseModuleServiceImpl(ArchiveMenuMapper archiveMenuMapper, ArchiveMetadataMapper archiveMetadataMapper) {
        this.archiveMenuMapper = archiveMenuMapper;
        this.archiveMetadataMapper = archiveMetadataMapper;
    }

    @Override
    public List<ArchiveMenuVO> getTreeMenus(Long userId) {
        log.info("用户ID({}), 请求档案树列表", userId);
        List<ArchiveMenuVO> allMenus = archiveMenuMapper.allMenu(); // 获取所有菜单的方法
        Map<Long, ArchiveMenuVO> menuMap = new HashMap<>(); // 用于快速查找菜单项

        // 将所有菜单放入Map中，便于后续快速定位
        for (ArchiveMenuVO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
        }

        // 从所有菜单中筛选出根节点（父ID为0）
        List<ArchiveMenuVO> rootList = new ArrayList<>();
        for (ArchiveMenuVO menu : allMenus) {
            if (menu.getParentId().equals(0L)) {
                rootList.add(menu);
                // 递归构建子菜单
                findChild(menu, menuMap);
            }
        }
        return rootList;
    }

    @Override
    public PageResult pageQueryBaseMetadata(ArchiveMenuPageQueryDTO archiveMenuPageQueryDto) {
        int page = archiveMenuPageQueryDto.getPage();
        int pageSize = archiveMenuPageQueryDto.getPageSize();
        PageHelper.startPage(page, pageSize);
        Page<ArchiveBaseMetadata> archiveBaseMetadata = archiveMetadataMapper.pageQuery(archiveMenuPageQueryDto);
        List<ArchiveBaseMetadataVO> archiveBaseMetadataVOS = archiveBaseMetadata.stream().map(metadata -> ArchiveBaseMetadataVO.builder()
                .id(metadata.getId())
                .archivalLevel(ArchivalLevelEnum.fromKey(metadata.getArchivalLevel()).getInfo())
                .archivalDeadline(ArchiveDeadlineEnum.fromKey(metadata.getArchivalDeadline()).getInfo())
                .archivalYear(metadata.getArchivalYear())
                .archivalCode(metadata.getArchivalCode())
                .archivalTitle(metadata.getArchivalTitle())
                .archivalDate(metadata.getArchivalDate())
                .archivalResponsible(JSONObject.parseObject(metadata.getArchivalResponsible(), BaseAdminInfo.class))
                .build()).toList();

        return new PageResult(archiveBaseMetadata.getTotal(), archiveBaseMetadataVOS);
    }

    @Override
    public ArchiveMetadataVO getArchiveMetadataById(Long id) {
        return archiveMetadataMapper.getArchiveMetadataById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editArchiveMetadata(EditArchiveMetadataDTO editArchiveMetadataDto) {
        ArchiveMetadata metadata = new ArchiveMetadata();
        BeanUtils.copyProperties(editArchiveMetadataDto, metadata);
        archiveMetadataMapper.update(metadata);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addArchiveMetadata(AddArchiveMetadataDTO addArchiveMetadataDto) {
        ArchiveMetadata metadata = new ArchiveMetadata();
        BeanUtils.copyProperties(addArchiveMetadataDto, metadata);
        archiveMetadataMapper.insert(metadata);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delArchiveMetadata(List<Long> ids) {
        if (ids.size() > 0) {
            DeletedStatus deletedStatus = DeletedStatus.builder()
                    .isDeleted(true).ids(ids).build();
            archiveMetadataMapper.updateArchiveDeleted(deletedStatus);
        }
    }


    /**
     * 递归查找子菜单
     * @param menu 当前菜单项
     * @param menuMap 所有菜单的Map映射，用于快速定位
     */
    private void findChild(ArchiveMenuVO menu, Map<Long, ArchiveMenuVO> menuMap) {
        List<ArchiveMenuVO> children = new ArrayList<>();
        for (ArchiveMenuVO child : menuMap.values()) {
            if (child.getParentId().equals(menu.getId())) {
                children.add(child);
                // 继续查找子菜单的子菜单
                findChild(child, menuMap);
            }
        }
        menu.setChildren(children);
    }

}
