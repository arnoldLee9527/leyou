package com.leyou.user.service.impl;

import com.leyou.config.User;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.service.UserService;
import com.leyou.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public Boolean CheckUser(String data, Integer type) {

        User user = new User();
        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;

        }

        int i = userMapper.selectCount(user);
        return i == 0;
    }

    @Override
    public Boolean Register(User user, String code) {

        //校验验证码是否正确
        String key = KEY_PREFIX+user.getPhone();

        String StoreCode = redisTemplate.opsForValue().get(key);
        if (StoreCode != null && StoreCode.equals(code)){
            //注册成功,将用户信息写入数据库

            //将用户密码加密
            //获取盐值
            String salt = CodecUtils.generateSalt();
            //加密密码
            String encodePassword = CodecUtils.md5Hex(user.getPassword(), salt);
            //保存用户
            user.setPassword(encodePassword);
            user.setSalt(salt);
            user.setCreated(new Date());
            boolean result = userMapper.insert(user) == 1;

            if (result){
                try {
                    //注册成功删除redis中的code
                    redisTemplate.delete(key);
                } catch (Exception e) {
                    LOGGER.error("redis删除失败，手机号：{}",user.getPhone());
                    e.printStackTrace();
                }
            }
            return true;
        }else {
            //注册失败
            return false;
        }
    }

    @Override
    public Boolean sendVerifyCode(String phone) {
        // 生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            // 发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            this.amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", msg);
            // 将code存入redis
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            LOGGER.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;
        }

    }
}
