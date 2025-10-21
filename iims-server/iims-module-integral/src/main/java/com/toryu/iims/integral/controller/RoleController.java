package com.toryu.iims.integral.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.toryu.iims.integral.model.dto.role.RoleDTO;
import com.toryu.iims.integral.model.dto.role.RolePageQueryDTO;
import com.toryu.iims.integral.model.vo.role.RoleVO;
import com.toryu.iims.integral.service.RoleService;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/iims/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }


    /**
     * 新增角色
     *
     * @param roleDto RoleDto
     * @return Result<String>
     */
    @PostMapping
    @ApiOperation("新增角色")
    @SaCheckPermission("permission:role:add")
    public Result<String> save(@RequestBody RoleDTO roleDto) {
        roleService.save(roleDto);
        return Result.success();
    }

    /**
     * 编辑角色
     *
     * @param roleDto RoleDto
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("编辑角色")
    @SaCheckPermission("permission:role:update")
    public Result<String> update(@RequestBody RoleDTO roleDto) {
        roleService.update(roleDto);
        return Result.success();
    }

    /**
     * 角色分页查询
     *
     * @param rolePageQueryDTO RolePageQueryDTO
     * @return Result<PageResult>
     */
    @PostMapping("/page")
    @ApiOperation("角色分页查询")
    @SaCheckPermission("permission:role:query")
    public Result<PageResult> page(@RequestBody RolePageQueryDTO rolePageQueryDTO) {
        PageResult pageResult = roleService.pageQuery(rolePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据id删除角色
     *
     * @param id 角色ID
     * @return Result<String>
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除角色")
    @SaCheckPermission("permission:role:delete")
    public Result<String> deleteById(@PathVariable Long id) {
        roleService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id查询角色
     *
     * @param id 角色ID
     * @return Result<RoleVO>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询角色")
    @SaCheckPermission("permission:role:query")
    public Result<RoleVO> getById(@PathVariable Long id) {
        RoleVO tags = roleService.getById(id);
        return Result.success(tags);
    }

    /**
     * 查询所有角色
     *
     * @return Result<List<RoleVO>>
     */
    @GetMapping("/list")
    @ApiOperation("查询所有角色")
    @SaCheckPermission("permission:role:query")
    public Result<List<RoleVO>> list() {
        List<RoleVO> list = roleService.list();
        return Result.success(list);
    }
}
