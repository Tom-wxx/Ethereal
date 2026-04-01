package com.ethereal.module.admin.controller;

import com.ethereal.common.result.R;
import com.ethereal.module.admin.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理后台-认证")
@RestController
@RequestMapping("/admin-api/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminUserService adminUserService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        return R.ok(adminUserService.adminLogin(body.get("username"), body.get("password")));
    }
}
