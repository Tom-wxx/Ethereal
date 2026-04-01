package com.ethereal.module.auth.controller;

import com.ethereal.common.constant.RedisKeyConstant;
import com.ethereal.common.result.R;
import com.ethereal.module.auth.dto.LoginDTO;
import com.ethereal.module.auth.dto.LoginVO;
import com.ethereal.module.auth.service.AuthService;
import com.ethereal.module.auth.service.SmsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "认证模块")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SmsService smsService;
    private final StringRedisTemplate redisTemplate;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms-code")
    public R<Void> sendSmsCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return R.fail(400, "手机号格式不正确");
        }
        smsService.sendSmsCode(phone);
        return R.ok();
    }

    @Operation(summary = "手机号登录/注册")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(authService.login(dto));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    /**
     * 仅开发/测试环境使用：获取 Redis 中存储的验证码
     */
    @Operation(summary = "[DEV] 获取验证码（仅测试用）")
    @GetMapping("/dev/sms-code/{phone}")
    public R<String> devGetSmsCode(@PathVariable String phone) {
        String code = redisTemplate.opsForValue().get(RedisKeyConstant.SMS_CODE + phone);
        if (code == null) return R.fail(404, "验证码不存在或已过期");
        return R.ok(code);
    }
}
