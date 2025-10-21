package com.toryu.iims.integral.service;

import com.toryu.iims.integral.model.dto.menu.MenuDTO;
import com.toryu.iims.integral.model.dto.menu.MenuPageQueryDTO;
import com.toryu.iims.integral.model.vo.admin.AdminMenuVO;
import com.toryu.iims.integral.model.vo.menu.MenuVO;
import com.toryu.iims.common.result.PageResult;

import java.util.List;

public interface MenuService {

    /**
     * 新增菜单
     * @param menuDto MenuDto
     */
    void save(MenuDTO menuDto);

    /**
     * 编辑菜单 MenuDto
     * @param menuDto MenuDto
     */
    void update(MenuDTO menuDto);

    /**
     * 菜单分页查询
     * @param menuPageQueryDTO MenuPageQueryDTO
     * @return PageResult
     */
    PageResult pageQuery(MenuPageQueryDTO menuPageQueryDTO);

    /**
     * 启用停用菜单
     * @param status 禁用状态
     * @param id 菜单ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据ID删除菜单
     * @param id 菜单ID
     */
    void deleteById(Long id);

    /**
     * 根据ID查询菜单
     * @param id 菜单ID
     * @return MenuVO
     */
    MenuVO getById(Long id);

    /**
     * 查询所有菜单
     * @return List<MenuVO>
     */
    List<MenuVO> list();

    /**
     * 查询树形菜单
     * @return List<AdminMenuVo>
     */
    List<AdminMenuVO> getTreeMenus(Long id);
}
