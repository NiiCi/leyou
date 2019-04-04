package com.leyou.item.filters;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JWTUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.item.pojo.FilterProperties;
import com.leyou.item.pojo.JwtProperties;
import com.netflix.discovery.converters.Auto;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Log4j2
@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtProperties prop;
    @Autowired
    private FilterProperties filterProp;

    /**
     * 声明过滤器类型为前置过滤器
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 指定过滤器的优先级
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 5;
    }

    /**
     * 需要过滤的操作
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取 request请求
        HttpServletRequest request = ctx.getRequest();
        // 获取请求路径
        String requestURI = request.getRequestURI();
        // 判断白名单
        return !isAllowPath(requestURI);
    }

    /**
     * 判断白名单
     *
     * @param requestURI
     * @return
     */
    private boolean isAllowPath(String requestURI) {
        boolean flag = false;
        log.info(filterProp.getAllowPaths());
        for (String path : filterProp.getAllowPaths()) {
            if (requestURI.startsWith(path)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 过滤器拦截的请求的操作
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        // zuul 下的包
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());
        //校验
        //解析token
        try {
            // 校验通过什么都不做，即放行
            UserInfo userInfo = JWTUtils.getInfoFromToken(token, prop.getPublicKey());
        } catch (Exception e) {
            // 校验出现异常，返回403
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(403);
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e);
        }
        return null;
    }
}
