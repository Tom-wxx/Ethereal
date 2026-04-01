package com.ethereal.module.admin.controller;

import com.ethereal.common.result.PageResult;
import com.ethereal.common.result.R;
import com.ethereal.module.admin.service.AdminUserService;
import com.ethereal.module.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台-用户管理")
@RestController
@RequestMapping("/admin-api/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "用户列表")
    @GetMapping
    public R<PageResult<User>> getUserList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return R.ok(adminUserService.getUserList(keyword, status, page, size));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public R<User> getUserDetail(@PathVariable Long id) {
        return R.ok(adminUserService.getUserDetail(id));
    }

    @Operation(summary = "封禁/解封用户")
    @PutMapping("/{id}/ban")
    public R<Void> banUser(@PathVariable Long id) {
        adminUserService.banUser(id);
        return R.ok();
    }

    @Operation(summary = "认证审核")
    @PutMapping("/{id}/verify")
    public R<Void> verifyUser(@PathVariable Long id) {
        adminUserService.verifyUser(id);
        return R.ok();
    }
}
