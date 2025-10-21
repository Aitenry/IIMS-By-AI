package com.toryu.iims.integral.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.toryu.iims.integral.model.dto.menu.MenuDTO;
import com.toryu.iims.integral.model.dto.menu.MenuPageQueryDTO;
import com.toryu.iims.integral.model.vo.admin.AdminMenuVO;
import com.toryu.iims.integral.model.vo.menu.MenuVO;
import com.toryu.iims.integral.service.AdminService;
import com.toryu.iims.integral.service.MenuService;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/iims/menu")
public class MenuController {

    private final MenuService menuService;
    private final AdminService adminService;

    public MenuController (MenuService menuService, AdminService adminService) {
        this.menuService = menuService;
        this.adminService = adminService;
    }

    /**
     * 新增菜单
     *
     * @param menuDto MenuDto
     * @return Result<String>
     */
    @PostMapping
    @ApiOperation("新增菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<String> save(@RequestBody MenuDTO menuDto) {
        menuService.save(menuDto);
        return Result.success();
    }

    /**
     * 编辑菜单
     *
     * @param menuDto MenuDto
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("编辑菜单")
    @SaCheckPermission("permission:menu:update")
    public Result<String> update(@RequestBody MenuDTO menuDto) {
        menuService.getById(menuDto.getId());
        menuService.update(menuDto);
        return Result.success();
    }

    /**
     * 菜单分页查询
     *
     * @param menuPageQueryDTO MenuPageQueryDTO
     * @return Result<PageResult>
     */
    @PostMapping("/page")
    @ApiOperation("菜单分页查询")
    @SaCheckPermission("permission:menu:query")
    public Result<PageResult> page(@RequestBody MenuPageQueryDTO menuPageQueryDTO) {
        PageResult pageResult = menuService.pageQuery(menuPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用停用菜单
     *
     * @param status Integer
     * @param id Long
     * @return Result<String>
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用停用菜单")
    @SaCheckPermission("permission:menu:update")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        menuService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据id删除菜单
     *
     * @param id Long
     * @return Result<String>
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除菜单")
    @SaCheckPermission("permission:menu:delete")
    public Result<String> deleteById(@PathVariable Long id) {
        menuService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询菜单
     *
     * @param id Long
     * @return Result<MenuVO>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<MenuVO> getById(@PathVariable Long id) {
        MenuVO menu = menuService.getById(id);
        return Result.success(menu);
    }

    /**
     * 查询所有菜单
     *
     * @return Result<List<MenuVO>>
     */
    @GetMapping("/list")
    @ApiOperation("查询所有菜单")
    @SaCheckPermission("permission:menu:query")
    public Result<List<MenuVO>> list() {
        List<MenuVO> list = menuService.list();
        return Result.success(list);
    }

    /**
     * 查询菜单树形结构（角色界面使用）
     *
     * @return Result<List<AdminMenuVo>>
     */
    @GetMapping("/menuTree")
    @ApiOperation("查询所有菜单")
    public Result<List<AdminMenuVO>> handleTree() {
        //查询当前用户的id
        long id = StpUtil.getLoginIdAsLong();
        List<AdminMenuVO> menusByUserId = menuService.getTreeMenus(id);
        return Result.success(menusByUserId);
    }

    /**
     * 查询所有菜单
     *
     * @return Result<List<AdminMenuVo>>
     */
    @GetMapping("/getMenuListByUserId")
    @ApiOperation("查询所有菜单")
    public Result<List<AdminMenuVO>> getMenuListByUserId() {
        long id = StpUtil.getLoginIdAsLong();
        List<AdminMenuVO> menusByUserId = adminService.getMenusByUserId(id);
        return Result.success(menusByUserId);
    }
}
