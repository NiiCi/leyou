package com.leyou.auth.pojo;


import com.leyou.auth.utils.RSAUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Log4j2
@ConfigurationProperties("ly.jwt")
public class JwtProperties {
    /**
     * JWT登录的用户名和密码
     */
    private String secret;
    /**
     * 公钥存放路径
     */
    private String pubKeyPath;

    /**
     * 私钥存放路径
     */
    private String priKeyPath;

    /**
     * token过期时间
     */
    private int expire;
    /**
     * cookie 名称
     */
    private String cookieName;
    /**
     * cookie 过期时间
     */
    private int cookieMaxAge;
    private PublicKey publicKey; // 公钥
    private PrivateKey privateKey; // 私钥


    // 下面的注解，使方法在构造函数执行完毕后执行
    @PostConstruct
    public void init(){
        try {
            File pubKey = new File(pubKeyPath);
            File priKey = new File(priKeyPath);
            if (!pubKey.exists() || !priKey.exists()) {
                // 生成公钥和私钥
                RSAUtils.generateKey(pubKeyPath, priKeyPath, secret);
            }
            // 获取公钥和私钥
            this.publicKey = RSAUtils.getPublicKey(pubKeyPath);
            this.privateKey = RSAUtils.getPrivateKey(priKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥和私钥失败！", e);
            throw new RuntimeException();
        }
    }
}
