package com.ethereal.module.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ethereal.common.exception.BusinessException;
import com.ethereal.module.auth.dto.LoginDTO;
import com.ethereal.module.auth.dto.LoginVO;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final SmsService smsService;

    @Transactional
    public LoginVO login(LoginDTO dto) {
        // 验证短信验证码
        if (!smsService.verifySmsCode(dto.getPhone(), dto.getCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 查找用户，不存在则自动注册
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone()));

        boolean isNew = false;
        if (user == null) {
            user = new User();
            user.setPhone(dto.getPhone());
            user.setNickname("Ethereal_" + dto.getPhone().substring(7));
            user.setStatus(1);
            user.setVerified(0);
            user.setVibeScore(0);
            user.setVipLevel(0);
            userMapper.insert(user);
            isNew = true;
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new BusinessException(403, "账号已被封禁");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // Sa-Token登录
        StpUtil.login(user.getId());

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserId(user.getId());
        vo.setIsNew(isNew);
        return vo;
    }

    public void logout() {
        StpUtil.logout();
    }
}
