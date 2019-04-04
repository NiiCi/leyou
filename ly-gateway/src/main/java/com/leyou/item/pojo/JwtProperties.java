package com.leyou.item.pojo;

import com.leyou.auth.utils.RSAUtils;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@Log4j2
@ConfigurationProperties(prefix="ly.jwt")
public class JwtProperties {

    private String pubKeyPath;// 公钥路径

    private PublicKey publicKey; // 公钥

    private String cookieName;

    @PostConstruct
    public void init(){
        try {
            // 获取公钥和私钥
            this.publicKey = RSAUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            log.error("初始化公钥失败！", e);
            throw new RuntimeException();
        }
    }

}
