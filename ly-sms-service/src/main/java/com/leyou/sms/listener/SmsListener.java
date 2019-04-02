package com.leyou.sms.listener;

import com.aliyuncs.CommonResponse;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Log4j2
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "ly.sms.queue",durable = "true"),
                    exchange = @Exchange(value = "ly.sms.exchange",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
                    key = {"sms.verify.code"}
            ))
    public void sendSms(Map<String, String> msg) throws Exception{
        if (msg == null || msg.size() < 1){
            return;
        }
        String phone = msg.get("phone");
        log.info(phone);
        String code = msg.get("code");
        log.info(code);
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            // 放弃处理
            return;
        }
        CommonResponse commonResponse = smsUtils.sendSms(phone,code);
        if (commonResponse.getHttpStatus() != 200 || commonResponse.getData() == null){
            throw new RuntimeException();
        }
    }
}
