package com.leyou.auth.controller;

import com.leyou.auth.pojo.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.utils.JWTUtils;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
@RequestMapping
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/accredit")
    public ResponseEntity<Void> authentication(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response){
        //登录校验
        String token =  authService.authentication(username,password);
        if (StringUtils.isBlank(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        //将token写入cookie，并指定httpOnly为tree，防止通过js获取和修改
        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge(),null,true);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证用户信息
     * @param token
     * @return
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token,
                                               HttpServletRequest request,HttpServletResponse response){
        try {
            // 获取token信息
            // 通过 公钥解密
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                System.out.println(cookie.getValue());
            }
            UserInfo userInfo = JWTUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //如果成功，需要刷新token返回给用户
            String newToken = JWTUtils.generateToken(userInfo,jwtProperties.getPrivateKey(),jwtProperties.getExpire());
            //将token 写入 cookie 中
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),newToken,jwtProperties.getCookieMaxAge(),null,true);
            // 成功后直接返回
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // 抛出异常，证明token无效，直接返回401
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
