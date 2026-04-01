package com.ethereal.module.discovery.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ethereal.common.result.R;
import com.ethereal.module.discovery.dto.DiscoveryCardVO;
import com.ethereal.module.discovery.dto.UserActionDTO;
import com.ethereal.module.discovery.service.DiscoveryService;
import com.ethereal.module.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "发现模块")
@RestController
@RequestMapping("/api/discovery")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;
    private final LikeService likeService;

    @Operation(summary = "获取推荐卡片")
    @GetMapping("/cards")
    public R<List<DiscoveryCardVO>> getCards(
            @RequestParam(required = false) Integer gender,
            @RequestParam(required = false) Integer ageMin,
            @RequestParam(required = false) Integer ageMax,
            @RequestParam(required = false) Double distance) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(discoveryService.getCards(userId, gender, ageMin, ageMax, distance));
    }

    @Operation(summary = "喜欢/跳过/超级喜欢")
    @PostMapping("/action")
    public R<Map<String, Object>> doAction(@Valid @RequestBody UserActionDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        boolean isMutual = likeService.doAction(userId, dto.getTargetUserId(), dto.getAction());
        return R.ok(Map.of("isMutual", isMutual));
    }
}
