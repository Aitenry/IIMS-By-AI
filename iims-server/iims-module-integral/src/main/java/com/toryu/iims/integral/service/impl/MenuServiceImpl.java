package com.toryu.iims.integral.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.toryu.iims.integral.mapper.MenuMapper;
import com.toryu.iims.integral.model.dto.menu.MenuDTO;
import com.toryu.iims.integral.model.dto.menu.MenuPageQueryDTO;
import com.toryu.iims.integral.model.vo.admin.AdminMenuVO;
import com.toryu.iims.integral.model.vo.menu.MenuVO;
import com.toryu.iims.integral.service.MenuService;
import com.toryu.iims.common.constant.MessageConstant;
import com.toryu.iims.common.constant.StatusConstant;
import com.toryu.iims.common.exception.FieldCannotEmptyException;
import com.toryu.iims.common.exception.NotFoundException;
import com.toryu.iims.common.model.entity.integral.Menu;
import com.toryu.iims.common.result.PageResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    public MenuServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(MenuDTO menuDto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDto, menu);
        //新增的菜单默认设置启用状态
        menu.setStatus(StatusConstant.ENABLE);
        menuMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MenuDTO menuDto) {
        Menu menu = new Menu();
        BeanUtils.copyProperties(menuDto, menu);
        if (menu.getId() == null) throw new FieldCannotEmptyException("id " + MessageConstant.FIELD_IS_NOT_NULL);
        menuMapper.update(menu);
    }

    @Override
    public PageResult pageQuery(MenuPageQueryDTO menuPageQueryDTO) {
        int page = menuPageQueryDTO.getPage();
        int pageSize = menuPageQueryDTO.getPageSize();
        PageHelper.startPage(page, pageSize);

        // select * from menu limit 0, 10
        Page<MenuVO> menu = menuMapper.pageQuery(menuPageQueryDTO);
        return new PageResult(menu.getTotal(), menu.getResult());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Integer status, Long id) {
        if (id == null) throw new FieldCannotEmptyException("id " + MessageConstant.FIELD_IS_NOT_NULL);
        Menu menu = new Menu();
        menu.setId(id);
        menu.setStatus(status);
        menuMapper.update(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Menu menu = menuMapper.getById(id);
        if (menu == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        menuMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MenuVO getById(Long id) {
        Menu menu = menuMapper.getById(id);
        if (menu == null) throw new NotFoundException(MessageConstant.DATA_NOT_FOUND);
        MenuVO menuVO = new MenuVO();
        BeanUtils.copyProperties(menu, menuVO);
        return menuVO;
    }

    @Override
    public List<MenuVO> list() {
        return menuMapper.all();
    }

    @Override
    public List<AdminMenuVO> getTreeMenus(Long userId) {
        List<AdminMenuVO> allMenus = menuMapper.allMenu(); // 获取所有菜单的方法
        Map<Long, AdminMenuVO> menuMap = new HashMap<>(); // 用于快速查找菜单项

        // 将所有菜单放入Map中，便于后续快速定位
        for (AdminMenuVO menu : allMenus) {
            menuMap.put(menu.getId(), menu);
        }

        // 从所有菜单中筛选出根节点（父ID为0）
        List<AdminMenuVO> rootList = new ArrayList<>();
        for (AdminMenuVO menu : allMenus) {
            if (menu.getParentId().equals(0L)) {
                rootList.add(menu);
                // 递归构建子菜单
                findChild(menu, menuMap);
            }
        }
        return rootList;
    }

    /**
     * 递归查找子菜单
     * @param menu 当前菜单项
     * @param menuMap 所有菜单的Map映射，用于快速定位
     */
    private void findChild(AdminMenuVO menu, Map<Long, AdminMenuVO> menuMap) {
        List<AdminMenuVO> children = new ArrayList<>();
        for (AdminMenuVO child : menuMap.values()) {
            if (child.getParentId().equals(menu.getId())) {
                children.add(child);
                // 继续查找子菜单的子菜单
                findChild(child, menuMap);
            }
        }
        menu.setChildren(children);
    }
}
