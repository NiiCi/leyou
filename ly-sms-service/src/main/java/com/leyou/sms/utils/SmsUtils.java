package com.leyou.sms.utils;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.sms.config.SmsProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@EnableConfigurationProperties(SmsProperties.class)
public class SmsUtils {
    @Autowired
    private SmsProperties smsProperties;

    public CommonResponse sendSms(String phone,String code)  throws ClientException{
        // reginid 地域id
        DefaultProfile profile = DefaultProfile.getProfile(smsProperties.getRegionId(), smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
        //创建一个DefaulrAcsClinet实例并初始化
        IAcsClient client = new DefaultAcsClient(profile);
        //创建API请求并设置参数
        CommonRequest request = new CommonRequest();
        //添加参数

        //请求方式
        request.setMethod(MethodType.POST);
        //产品域名，开发者无需替换
        request.setDomain("dysmsapi.aliyuncs.com");
        //阿里大于版本
        request.setVersion(smsProperties.getVersion());
        //发送短信的方式
        request.setAction("SendSms");

        //PhoneNumbers,必填
        request.putQueryParameter("PhoneNumbers",phone);
        //短信签名，在阿里云控制台中查看
        request.putQueryParameter("SignName",smsProperties.getSignName());
        //短信模板ID。请在控制台模板管理页面模板CODE一列查看
        request.putQueryParameter("TemplateCode",smsProperties.getTemplateCode());
        //短信模板变量对应的实际值，JSON格式
        request.putQueryParameter("TemplateParam","{\"code\":\""+ code + "\"}");
        //发起请求并处理应答
        CommonResponse response = null;
        try {
            response = client.getCommonResponse(request);
            log.info("发送短信状态：{}", response.getHttpStatus());
            log.info("发送短信消息：{}", response.getData());
        } catch (ClientException e) {
           log.error(e.getMessage(),e);
        }
        return response;
    }
}
