package com.leyou.cart.interceptor;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JWTUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private JwtProperties jwtProperties;

    //定义一个线程域，存放登录用户
    /**
     * ThreadLocal 存放用户信息，线程内共享，因此请求到达 controller 后可以共享 userInfo
     */
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    public LoginInterceptor(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询token
        String token = CookieUtils.getCookieValue(request,"LY_TOKEN");
        log.info(token);
        if (StringUtils.isBlank(token)){
            //未登录，返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        //有token，查询用户信息
        try {
            //解析成功，证明已经登录
            UserInfo userInfo = JWTUtils.getInfoFromToken(token,jwtProperties.getPublicKey());
            //放入线程域
            tl.set(userInfo);
            return true;
        } catch (Exception e) {
            //抛出异常，证明未登录，返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        tl.remove();
    }

    public static UserInfo getLoginUser() {
        return tl.get();
    }

}
