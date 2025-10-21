package com.toryu.iims.integral.mapper;

import com.github.pagehelper.Page;
import com.toryu.iims.integral.model.dto.admin.AdminLoginDTO;
import com.toryu.iims.integral.model.dto.admin.AdminPageQueryDTO;
import com.toryu.iims.integral.model.vo.admin.AdminMenuVO;
import com.toryu.iims.integral.model.vo.admin.AdminVO;
import com.toryu.iims.integral.model.vo.role.RoleVO;
import com.toryu.iims.common.annotation.AutoFill;
import com.toryu.iims.common.enums.OperationType;
import com.toryu.iims.common.model.entity.base.BaseAdminInfo;
import com.toryu.iims.common.model.entity.integral.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface AdminMapper {

    /**
     * 新增管理员
     *
     * @param admin Admin
     */
    @AutoFill(value = OperationType.INSERT)
    int insert(Admin admin);

    /**
     * 根据用户名或者邮箱查询用户
     *
     * @return Admin
     */
    Admin getByUserNameOrEmail(AdminLoginDTO adminLoginDto);

    /**
     * 分页查询
     *
     * @param adminPageQueryDTO AdminPageQueryDTO
     * @return Page<AdminVO>
     */
    Page<AdminVO> pageQuery(AdminPageQueryDTO adminPageQueryDTO);

    /**
     *根据主键动态修改属性
     * @param admin Admin
     */
    @AutoFill(value = OperationType.UPDATE)
    int update(Admin admin);

    /**
     * 根据id查询管理员信息
     * @param id 管理员ID
     * @return Admin
     */
    @Select("select * from iims_integral_admin where id = #{id}")
    Admin selectObjectById(Long id);

    List<Admin> selectObjectByIds(List<Long> ids);

    @Select("select avatar from iims_integral_admin where id = #{id}")
    Long getAvatarById(Long id);

    /**
     * 根据用户id查询权限集合
     * @param userId 管理员ID
     * @return Set<String>
     */
    Set<String> searchUserPermissions(String userId);

    /**
     * 根据用户id查询角色集合
     * @param userId 管理员ID
     * @return List<RoleVO>
     */
    List<RoleVO> searchUserRoles(String userId);

    /**
     * 根据用户id查询菜单集合
     * @param userId 管理员ID
     * @return List<AdminMenuVo>
     */
    List<AdminMenuVO> getMenusByUserId(Long userId);

    /**
     * 根据管理员ID获取管理员所拥有的角色名称
     * @param ids 管理员ID
     * @return List<String>
     */
    List<String> getRoleNameByIds(List<String> ids);

    /**
     * 查询所有管理员
     * @return List<Admin>
     */
    @Select("select * from iims_integral_admin")
    List<Admin> list();

    @Select("select id, username from iims_integral_admin where username like concat('%', #{username}, '%')")
    List<BaseAdminInfo> baseInfoQuery(String username);
}
