package com.toryu.iims.integral.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSONArray;
import com.toryu.iims.common.model.dto.BaseAdminInfoQueryDTO;
import com.toryu.iims.common.model.entity.base.BaseAdminInfo;
import com.toryu.iims.common.model.entity.integral.Admin;
import com.toryu.iims.common.model.vo.BaseAdminInfoVO;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.model.dto.admin.AdminDTO;
import com.toryu.iims.integral.model.dto.admin.AdminLoginDTO;
import com.toryu.iims.integral.model.dto.admin.AdminPageQueryDTO;
import com.toryu.iims.integral.model.dto.admin.AdminPasswordDTO;
import com.toryu.iims.integral.model.entity.PublicKey;
import com.toryu.iims.integral.model.vo.admin.AdminVO;
import com.toryu.iims.integral.model.vo.role.RoleVO;
import com.toryu.iims.integral.service.AdminService;
import com.toryu.iims.integral.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 管理员
 */
@RestController
@RequestMapping("/iims/admin")
@Slf4j
@Api(tags = "管理员")
public class AdminController implements Serializable {


    private final AdminService adminService;

    private final RoleService roleService;

    public AdminController(AdminService adminService, RoleService roleService) {
        this.adminService = adminService;
        this.roleService = roleService;
    }

    /**
     * 管理员注册
     *
     * @param adminRegisterDto AdminDto
     * @return Result<String>
     */
    @PostMapping()
    @ApiOperation("管理员注册")
    public Result<String> save(@RequestBody AdminDTO adminRegisterDto) {
        adminService.save(adminRegisterDto);
        return Result.success("成功新增管理员");
    }

    @GetMapping("/login/key")
    public Result<PublicKey> getPublicKey() {
        return Result.success(adminService.getPublicKey());
    }

    /**
     * 管理员登录
     *
     * @param adminLoginDto AdminLoginDto
     * @return Result<AdminVO>
     */
    @PostMapping("/login")
    @ApiOperation("管理员登录")
    public Result<Map<String, Object>> login(@RequestBody @Valid AdminLoginDTO adminLoginDto) {
        Admin admin = adminService.login(adminLoginDto);
        //登录成功后，生成jwt令牌
        String uid = String.valueOf(admin.getId());
        String token = null;
        List<String> permissions = null;
        if (uid != null) {
            StpUtil.logout(uid, "Web");
            StpUtil.login(uid, "Web");
            token = StpUtil.getTokenValueByLoginId(uid, "Web");
            permissions = StpUtil.getPermissionList(uid);
        }

        assert uid != null;
        List<Long> roleIds = JSONArray.parseArray(admin.getRole(), Long.class);
        String roleName = null;
        if (Objects.nonNull(roleIds) && !roleIds.isEmpty()) {
            RoleVO roleVo = roleService.getById(roleIds.get(0));
            roleName = roleVo.getRoleName();
        }
        AdminVO adminVo = AdminVO.builder()
                .id(Long.valueOf(uid))
                .username(admin.getUsername())
                .name(admin.getName())
                .role(roleName)
                .imageUrl(admin.getImageUrl())
                .token(token)
                .permissions(permissions)
                .build();

        return Result.successWithNonNull(adminVo);
    }

    @GetMapping("/logout")
    @ApiOperation("用户退出")
    public Result<String> logout() {
        //从令牌中解密出来userId
        long id = StpUtil.getLoginIdAsLong();
        //销毁令牌
        StpUtil.logout(id, "Web");
        return Result.success();
    }

    /**
     * 管理员分页查询
     *
     * @param adminPageQueryDTO 查询管理员分类数据
     * @return Result<PageResult>
     */
    @PostMapping("/page")
    @ApiOperation("管理员分页查询")
    @SaCheckPermission("permission:admin:query")
    public Result<PageResult> page(@RequestBody AdminPageQueryDTO adminPageQueryDTO) {
        PageResult pageResult = adminService.pageQuery(adminPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/base/page")
    @ApiOperation("管理员分页查询")
    @SaCheckPermission("permission:admin:query")
    public Result<List<BaseAdminInfo>> info(@RequestBody BaseAdminInfoQueryDTO baseInfoQueryDto) {
        List<BaseAdminInfo> result = adminService.baseInfoQuery(baseInfoQueryDto);
        return Result.success(result);
    }

    /**
     * 启用禁用管理员账号
     *
     * @param isDisable 禁用状态
     * @param id 用户id
     * @return Result<String>
     */
    @PostMapping("/status/{isDisable}")
    @ApiOperation("启用禁用管理员账号")
    public Result<String> startOrStop(@PathVariable Boolean isDisable, Long id) {
        adminService.startOrStop(isDisable, id);
        return Result.success();
    }

    /**
     * 编辑管理员信息
     *
     * @param adminDto AdminDto
     * @return Result<String>
     */
    @PutMapping
    @ApiOperation("编辑管理员信息")
    public Result<String> update(@RequestBody AdminDTO adminDto) {
        adminService.update(adminDto);
        return Result.success();
    }

    /**
     * 根据id查询管理员信息
     *
     * @param id 管理员ID
     * @return Result<Admin>
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询管理员信息")
    public Result<Admin> getById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        return Result.success(admin);
    }

    @GetMapping("/base/info/{id}")
    @ApiOperation("根据id查询基础管理员信息")
    public Result<BaseAdminInfoVO> getAdminBaseInfoById(@PathVariable Long id) {
        BaseAdminInfoVO admin = adminService.getAdminBaseInfoById(id);
        return Result.success(admin);
    }

    /**
     * 根据id删除管理员
     *
     * @param id ID
     * @return Result<String>
     */
    @DeleteMapping("/{id}")
    @ApiOperation("根据id删除管理员")
    @SaCheckPermission("permission:admin:delete")
    public Result<String> deleteById(@PathVariable Long id) {
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param adminPasswordDto AdminPasswordDto
     * @return Result<String>
     */
    @PutMapping("/password")
    @ApiOperation("修改密码")
    @CacheEvict(value = "adminCache", allEntries = true)
    public Result<String> updatePassword(@RequestBody AdminPasswordDTO adminPasswordDto) {
        adminService.updatePassword(adminPasswordDto);
        return Result.success();
    }

    /**
     * 导出模板
     *
     * @param response HttpServletResponse
     */
    @GetMapping("/export")
    @ApiOperation("导出模板")
    public Result<String> export(HttpServletResponse response) {
        adminService.exportBusinessData(response);
        return Result.success();
    }

    /**
     * 导入用户数据报表
     *
     * @param file MultipartFile
     * @return Result<List<AdminVO>>
     */
    @PostMapping("/import")
    @ApiOperation("导入用户数据报表")
    public Result<List<AdminVO>> importUser(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        //通过输入流读取指定的Excel文件
        XSSFWorkbook excel = new XSSFWorkbook(in);
        //获取Excel文件的第1个Sheet页
        XSSFSheet sheet = excel.getSheet("Sheet1");

        //获取Sheet页中的最后一行的行号
        int lastRowNum = sheet.getLastRowNum();

        ArrayList<AdminVO> list = new ArrayList<>();

        for (int i = 2; i <= lastRowNum; i++) {
            AdminVO admin = new AdminVO();

            //获取Sheet页中的行
            XSSFRow titleRow = sheet.getRow(i);

            //真实姓名
            XSSFCell cell2 = titleRow.getCell(0);
            String name = cell2.getStringCellValue();
            admin.setName(name);

            //用户名
            XSSFCell cell3 = titleRow.getCell(1);
            String username = cell3.getStringCellValue();
            admin.setUsername(username);

            //邮箱
            XSSFCell cell5 = titleRow.getCell(3);
            String email = cell5.getStringCellValue();
            admin.setEmail(email);

            //手机号
            XSSFCell cell6 = titleRow.getCell(4);
            String phone = cell6.getStringCellValue();
            admin.setPhone(phone);

            //头像
            XSSFCell cell7 = titleRow.getCell(5);
            String avatar = cell7.getStringCellValue();
            admin.setAvatar(Long.valueOf(avatar));

            //性别
            XSSFCell cell8 = titleRow.getCell(6);
            String sex = cell8.getStringCellValue();
            if (sex.equals("男")) {
                sex = "1";
            } else {
                sex = "2";
            }
            admin.setSex(sex);

            //角色
            admin.setRole("[11]");

            list.add(admin);
        }

        //关闭资源
        in.close();
        excel.close();

        return Result.success(list);
    }
}
