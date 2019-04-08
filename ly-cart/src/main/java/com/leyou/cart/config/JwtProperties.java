package com.leyou.cart.config;

import com.leyou.auth.utils.RSAUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@ConfigurationProperties(prefix = "ly.jwt")
@Data
@Log4j2
public class JwtProperties {
    private String pubKeyPath;// 公钥路径
    private PublicKey publicKey; // 公钥key
    private String cookieName; // cookie名称

    /**
     * 公钥key 初始化
     */
    @PostConstruct
    public void init(){
        try {
            //获取公钥key
            this.publicKey = RSAUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败!",e);
            throw new RuntimeException();
        }
    }
}
