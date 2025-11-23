package com.toryu.iims.integral.service;

import com.toryu.iims.common.model.dto.BaseAdminInfoQueryDTO;
import com.toryu.iims.common.model.entity.base.BaseAdminInfo;
import com.toryu.iims.common.model.entity.integral.Admin;
import com.toryu.iims.common.model.vo.BaseAdminInfoVO;
import com.toryu.iims.common.result.PageResult;
import com.toryu.iims.integral.model.dto.admin.AdminDTO;
import com.toryu.iims.integral.model.dto.admin.AdminLoginDTO;
import com.toryu.iims.integral.model.dto.admin.AdminPageQueryDTO;
import com.toryu.iims.integral.model.dto.admin.AdminPasswordDTO;
import com.toryu.iims.integral.model.entity.PublicKey;
import com.toryu.iims.integral.model.vo.admin.AdminMenuVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;

public interface AdminService {

    /**
     * 新增管理员
     * @param adminRegisterDto AdminDto
     */
    void save(AdminDTO adminRegisterDto);

    /**
     * 管理员登录
     * @param adminLoginDto AdminLoginDto
     * @return Admin
     */
    Admin login(AdminLoginDTO adminLoginDto);

    /**
     * 分页查询
     * @param adminPageQueryDTO AdminPageQueryDTO
     * @return PageResult
     */
    PageResult pageQuery(AdminPageQueryDTO adminPageQueryDTO);

    /**
     * 启用禁用管理员账号
     * @param isDisable 禁用状态
     * @param id 管理员ID
     */
    void startOrStop(Boolean isDisable, Long id);

    /**
     * 编辑管理员信息
     * @param adminDto AdminDto
     */
    void update(AdminDTO adminDto);

    /**
     * 根据id查询管理员信息
     * @param id 管理员ID
     * @return Admin
     */
    Admin getById(Long id);

    /**
     * 根据ID删除管理员
     * @param id 管理员ID
     */
    void deleteById(Long id);

    /**
     * 根据用户id查询菜单集合
     * @return List<AdminMenuVo>
     */
    List<AdminMenuVO> getMenusByUserId(Long id);

    /**
     * 修改密码
     * @param adminPasswordDto AdminPasswordDto
     */
    void updatePassword(AdminPasswordDTO adminPasswordDto);

    /**
     * 导出运营数据报表
     * @param response HttpServletResponse
     */
    void exportBusinessData(HttpServletResponse response);

    List<BaseAdminInfo> baseInfoQuery(BaseAdminInfoQueryDTO baseInfoQueryDto);

    BaseAdminInfoVO getAdminBaseInfoById(Long id);

    List<BaseAdminInfoVO> getAdminBaseInfoByIds(List<Long> ids);

    HashMap<Long, BaseAdminInfoVO> getAdminBaseHashInfoByIds(List<Long> ids);

    PublicKey getPublicKey();
}
