package com.atguigu.lease.web.admin.custom.intercepter;

import com.atguigu.lease.common.context.LoginUser;
import com.atguigu.lease.common.context.LoginUserContext;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.common.utils.JwtUtil;
import com.atguigu.lease.web.admin.service.impl.SystemUserServiceImpl;
import com.atguigu.lease.web.admin.vo.system.user.SystemUserInfoVo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/*
认证：Authentication
*   AuthenticationInterceptor 认证（登录）拦截器 =》判断请求是否登录
 */
    /*
     *  /admin/info
     *  拦截器前端项目发送的请求----> 判断请求是否合法(是否登录(有没有合法的token))
     *
     *  tomcat: web容器
     *    3大作用域: 请求域 会话域  应用域
     *
     * */
    @Component
    public class AdminInterceptor implements HandlerInterceptor {

        /*
        /判断前端发过来的请求 判断请求是否合法
         */

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String token = request.getHeader("access_token");

            if (token == null) {
                throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);//发送请求的客户端没有登录系统
            } else {//token是存在的 使用parseToken 验证token是否过期
                Claims claims = JwtUtil.parseToken(token);

                Long userId = (Long) claims.get("userId", Long.class);
                String userName = (String) claims.get("userName",String.class);
                //把负载数据保存到请求域理
//                request.setAttribute("userId", userId);
                //把负载数据保存到当前线程
//                ThreadLocal threadLocal = new ThreadLocal();
//                threadLocal.set(userId);
//                threadLocal.set(userName);

                LoginUserContext.setLoginUser(new LoginUser(userId,userName));
            }
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            LoginUserContext.removeUser();
        }


}
