package com.toryu.iims.auth.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.toryu.iims.common.context.BaseContext;
import com.toryu.iims.common.exception.IsTokenException;
import com.toryu.iims.common.model.entity.integral.Admin;
import com.toryu.iims.common.properties.JwtProperties;
import com.toryu.iims.integral.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final AdminService adminService;

    public JwtTokenInterceptor(JwtProperties jwtProperties, AdminService adminService) {
        this.jwtProperties = jwtProperties;
        this.adminService = adminService;
    }

    /**
     * 校验jwt
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param handler Object
     * @return boolean
     */
    public boolean preHandle(
            @NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull Object handler) {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        // 从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());
        if (token == null) throw new IsTokenException("Token不存在");

        // 校验令牌
        try {
            long id = StpUtil.getLoginIdAsLong();
            Admin admin = adminService.getById(id);
            if (admin.getIsDisable())
                throw new IsTokenException("账号锁定");
            if (admin.getIsDeleted())
                throw new IsTokenException("账号已被移除");
            // 将用户id存储到ThreadLocal
            BaseContext.setCurrentId(id);
            // 通过，放行
            return true;
        } catch (Exception ex) {
            throw new IsTokenException(ex.getMessage());
        }
    }
}
