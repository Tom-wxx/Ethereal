package com.ethereal.module.auth.service;

import com.ethereal.common.constant.RedisKeyConstant;
import com.ethereal.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final StringRedisTemplate redisTemplate;

    public void sendSmsCode(String phone) {
        // 频率限制：同一手机号60秒内只能发一次
        String limitKey = RedisKeyConstant.SMS_LIMIT + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(limitKey))) {
            throw new BusinessException(429, "发送过于频繁，请稍后再试");
        }

        // 生成6位数验证码
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));

        // 存储验证码到Redis，5分钟有效
        String codeKey = RedisKeyConstant.SMS_CODE + phone;
        redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);

        // 设置频率限制，60秒
        redisTemplate.opsForValue().set(limitKey, "1", 60, TimeUnit.SECONDS);

        // TODO: 对接阿里云短信服务发送验证码
        // 开发环境直接打印验证码
        log.info("【Ethereal】手机号: {} 验证码: {}", phone, code);
    }

    public boolean verifySmsCode(String phone, String code) {
        String codeKey = RedisKeyConstant.SMS_CODE + phone;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(codeKey);
            return true;
        }
        return false;
    }
}
