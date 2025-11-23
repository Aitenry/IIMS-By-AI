package com.toryu.iims.integral.controller;

import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.model.vo.organization.OrganizationMenuVO;
import com.toryu.iims.integral.model.vo.organization.OrganizationVO;
import com.toryu.iims.integral.service.OrganizationService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: Aitenry
 * @Date: 2025/11/15 23:58
 * @Version: v1.0.0
 * @Description: TODO
 **/
@RestController
@Slf4j
@RequestMapping("/iims/organization")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/menuTree")
    @ApiOperation("查询所有菜单")
    public Result<List<OrganizationMenuVO>> handleTree(@RequestParam(required = false) Long companyId) {
        List<OrganizationMenuVO> treeMenus = organizationService.getTreeMenus(companyId);
        return Result.success(treeMenus);
    }

    @GetMapping("/allMenuTree")
    @ApiOperation("查询所有菜单")
    public Result<List<OrganizationVO>> handleAllTree() {
        List<OrganizationVO> treeMenus = organizationService.getAllMenuList();
        return Result.success(treeMenus);
    }



}
