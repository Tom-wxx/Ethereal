package com.ethereal.module.admin.service;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ethereal.common.exception.BusinessException;
import com.ethereal.common.result.PageResult;
import com.ethereal.module.admin.entity.Admin;
import com.ethereal.module.admin.mapper.AdminMapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final AdminMapper adminMapper;
    private final UserMapper userMapper;

    public Map<String, Object> adminLogin(String username, String password) {
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
        if (admin == null || !BCrypt.checkpw(password, admin.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (admin.getStatus() != 1) {
            throw new BusinessException(403, "账号已禁用");
        }

        // 使用不同的登录类型区分管理员
        StpUtil.login("admin:" + admin.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("role", admin.getRole());
        return result;
    }

    public PageResult<User> getUserList(String keyword, Integer status, int page, int size) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(User::getNickname, keyword).or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> p = userMapper.selectPage(new Page<>(page, size), wrapper);

        PageResult<User> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setCurrent(p.getCurrent());
        result.setSize(p.getSize());
        return result;
    }

    public User getUserDetail(Long id) {
        return userMapper.selectById(id);
    }

    public void banUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setStatus(user.getStatus() == 1 ? 2 : 1);
        userMapper.updateById(user);
    }

    public void verifyUser(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) throw new BusinessException(404, "用户不存在");
        user.setVerified(1);
        userMapper.updateById(user);
    }
}
