package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 大于短信服务属性类
 */
@Data
@ConfigurationProperties(prefix="ly.sms")
public class SmsProperties {
   private String accessKeyId;
   private String accessKeySecret;
    /**
     * 签名
     */
   private String signName;
    /**
     * 模板code
     */
   private String templateCode;
    /**
     * 地域id
     */
   private String regionId;
    /**
     * 大于短信服务版本
     */
   private String version;
}
