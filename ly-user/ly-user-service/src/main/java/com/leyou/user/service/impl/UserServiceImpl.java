package com.leyou.user.service.impl;

import com.leyou.common.utils.CodeUtils;
import com.leyou.common.utils.Md5Utils;
import com.leyou.user.dao.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("userService")
@Log4j2
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //redis 短信 key, : 代表目录
    static final String KEY_PREFIX = "user:code:phone:";

    /**
     * 判断用户名是否存在
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkUserData(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        // 判断记录数 如果为0 代表用户名可用，1代表用户名或手机号已经被注册
        return userMapper.selectCount(user) == 0;
    }

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    @Override
    public Boolean getCode(String phone) {
        //生成验证码
        String code = CodeUtils.generateCode(6);
        try {
            //发送短信
            Map<String, String> params = new HashMap<>();
            params.put("phone", phone);
            params.put("code", code);
            //向mq中发送消息
            amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", params);
            //将code存入redis,设置超时时间为5分钟
            redisTemplate.opsForValue().set(KEY_PREFIX+phone,code,1,TimeUnit.HOURS);
            return true;
        } catch (Exception e) {
            log.error("发送短信失败,phone: {}, code: {}",phone,code);
            return false;
        }
    }

    /**
     * 注册
     * @param user
     * @param code
     * @return
     */
    @Override
    public Boolean register(User user, String code) {
        String key = KEY_PREFIX + user.getPhone();
        // 从redis中取出验证码
        String codeCache = redisTemplate.opsForValue().get(key);
        // 校验验证码是否正确
        if (!code.equals(codeCache)){
            return false;
        }
        user.setCreated(new Date());
        // 生成盐
        String salt = CodeUtils.generateSalt();
        log.info(salt);
        user.setSalt(salt);
        //对密码进行加密
        user.setPassword(Md5Utils.encryptPassword(user.getPassword(),salt));
        //写入数据库
        Boolean flag = userMapper.insertSelective(user) == 1;
        //如果注册成功，删除redis中的code
        if (flag){
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.error("删除缓存验证码失败,code: {} "+code,e);
            }
        }
        return flag;
    }

    /**
     * 查询用户
     * @param username
     * @param password
     * @return
     */
    @Override
    public User queryUser(String username, String password) {
        //查询
        User user = new User();
        user.setUsername(username);
        User record = userMapper.selectOne(user);
        if (record == null){
            return null;
        }
        //校验密码
        if (!record.getPassword().equals(Md5Utils.encryptPassword(password,record.getSalt()))){
            return null;
        }
        return record;
    }
}
