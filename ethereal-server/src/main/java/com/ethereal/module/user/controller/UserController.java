package com.ethereal.module.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ethereal.common.result.R;
import com.ethereal.module.user.dto.UserProfileDTO;
import com.ethereal.module.user.dto.UserProfileVO;
import com.ethereal.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户资料")
    @GetMapping("/profile")
    public R<UserProfileVO> getProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(userService.getProfile(userId));
    }

    @Operation(summary = "更新用户资料")
    @PutMapping("/profile")
    public R<Void> updateProfile(@Valid @RequestBody UserProfileDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.updateProfile(userId, dto);
        return R.ok();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public R<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = StpUtil.getLoginIdAsLong();
        String url = userService.uploadAvatar(userId, file);
        return R.ok(url);
    }

    @Operation(summary = "更新位置")
    @PutMapping("/location")
    public R<Void> updateLocation(@RequestBody Map<String, BigDecimal> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.updateLocation(userId, body.get("longitude"), body.get("latitude"));
        return R.ok();
    }

    @Operation(summary = "更新兴趣标签")
    @PostMapping("/tags")
    public R<Void> updateTags(@RequestBody Map<String, List<String>> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        userService.updateTags(userId, body.get("tags"));
        return R.ok();
    }

    @Operation(summary = "查看用户详情")
    @GetMapping("/{id}")
    public R<UserProfileVO> getUserDetail(@PathVariable Long id) {
        return R.ok(userService.getUserDetail(id));
    }
}
