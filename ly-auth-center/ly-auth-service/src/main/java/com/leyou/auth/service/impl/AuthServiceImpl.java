package com.leyou.auth.service.impl;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.pojo.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JWTUtils;
import com.leyou.user.pojo.User;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("authService")
@EnableConfigurationProperties(JwtProperties.class)
@Log4j2
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;
    @Override
    public String authentication(String username, String password) {
        //查询用户
        ResponseEntity<User> resp  = userClient.query(username,password);
        if (!resp.hasBody()){
            log.error("用户信息不存在，{}",username);
            return null;
        }
        // 获取登录用户
        User user = resp.getBody();
        //生成token
        String token = null;
        try {
            token = JWTUtils.generateToken(
                    new UserInfo(user.getId(), user.getUsername()),
                    prop.getPrivateKey(), prop.getExpire());
            return token;
        } catch (Exception e) {
           log.error(e.getMessage(),e);
           return null;
        }
    }
}
