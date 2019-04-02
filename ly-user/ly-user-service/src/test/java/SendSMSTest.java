import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.leyou.user.UserApplication;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@Log4j2
@SpringBootTest(classes = UserApplication.class)
public class SendSMSTest {
    /**
     * 注入 StringRedisTemplate
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String AccessKeyId = "LTAIBN5V5Bn59q19";
    private static final String AccessSecret = "8X1ng6hXtCFszmTdCo4vFiOK5muuJ0";
    private static final String RegionID = "cn-hangzhou";
    private static final String version = "2017-05-25";

    public static void main(String[] args) {
        sendSMS("17826877753");
    }

    public static void sendSMS(String phone){
        // reginid 地域id
        DefaultProfile profile = DefaultProfile.getProfile(RegionID, AccessKeyId, AccessSecret);
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
        request.setVersion(version);
        //发送短信的方式
        request.setAction("SendSms");

        //PhoneNumbers,必填
        request.putQueryParameter("PhoneNumbers",phone);
        //短信签名，在阿里云控制台中查看
        request.putQueryParameter("SignName","乐优商城");
        //短信模板ID。请在控制台模板管理页面模板CODE一列查看
        request.putQueryParameter("TemplateCode","SMS_162520210");
        //短信模板变量对应的实际值，JSON格式
        request.putQueryParameter("TemplateParam","{code:'1111'}");
        //发起请求并处理应答或异常
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getHttpStatus());
        } catch (ClientException e) {
            log.error(e.getMessage(),e);
        }
    }

    @Test
    public void testRedis(){
        //存储 string类型的数据
        redisTemplate.opsForValue().set("test","redis");
        //获取数据
        String val = redisTemplate.opsForValue().get("test");
        System.out.println(val);
    }

    @Test
    public void testRedis2() {
        // 存储数据，并指定剩余生命时间,5个小时,第三个参数为时间,最后一个参数为单位
        this.redisTemplate.opsForValue().set("key2", "value2",
                5, TimeUnit.HOURS);
    }

    /**
     * redis 操作 hash
     */
    @Test
    public void testHash(){
        // redis中 存入 key 为user的 hash 数据
        BoundHashOperations<String, String, Object> hashOps =
                this.redisTemplate.boundHashOps("user");
        // 操作hash数据
        hashOps.put("name", "jack");
        hashOps.put("age", "21");

        // 获取单个数据
        Object name = hashOps.get("name");
        System.out.println("name = " + name);

        // 获取所有数据
        Map<String, Object> map = hashOps.entries();

        for (Map.Entry<String, Object> me : map.entrySet()) {
            System.out.println(me.getKey() + " : " + me.getValue());
        }
    }

}
