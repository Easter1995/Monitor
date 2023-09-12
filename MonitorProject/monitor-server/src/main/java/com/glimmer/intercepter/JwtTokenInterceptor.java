package com.glimmer.intercepter;


import com.glimmer.constant.JwtClaimsConstant;
import com.glimmer.context.BaseContext;
import com.glimmer.properties.JwtProperties;
import com.glimmer.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器，SpringMVC的相关组件
 * 每次请求都会进入到拦截器，拦截器会根据jwt验权情况考虑是否放行
 * @Component即把拦截器交于Spring管理
 * @Slf4j 日志管理
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        /*
        * 先判断是否是用户登陆请求
        * 如果是的话就需要放行，因为在用户登录后才会发放jwt的token
        * 根据请求的路径以及请求方法来判断
         */
        String servletPath = request.getServletPath();
        String method = request.getMethod();
        if("/usr".equals(servletPath) && "POST".equals(method)) {
            //如果是拦截到的是登录的请求则对其放行
            return true;
        }


        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Integer userId = Integer.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            Integer role = Integer.valueOf(claims.get(JwtClaimsConstant.USER_ROLE).toString());
            log.info("当前用户id：{}", userId);
            log.info("当前用户角色：{}; 0为普通用户，1为管理员",role);
            //将用户id存入全局上下文中
            BaseContext.setCurrentId(userId);
            BaseContext.setCurrentRole(role);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
