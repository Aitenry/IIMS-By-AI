package com.toryu.iims.auth.config.token;

import cn.dev33.satoken.stp.StpInterface;
import com.toryu.iims.integral.mapper.AdminMapper;
import com.toryu.iims.integral.model.vo.role.RoleVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private AdminMapper adminMapper;

    /**
     * 返回一个用户所拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object userId, String loginType) {
        String uid = (String) userId;
        Set<String> set = adminMapper.searchUserPermissions(uid);
        return new ArrayList<>(set);
    }

    /**
     * 返回一个用户所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        String userId = (String) loginId;
        List<RoleVO> roleVOS = adminMapper.searchUserRoles(userId);
        return roleVOS.stream().map(RoleVO::getRoleName).collect(Collectors.toList());
    }
}