package com.ethereal.module.like.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ethereal.common.result.PageResult;
import com.ethereal.common.result.R;
import com.ethereal.module.like.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "喜欢模块")
@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "谁喜欢我")
    @GetMapping("/who-likes-me")
    public R<PageResult<Map<String, Object>>> getWhoLikesMe(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(likeService.getWhoLikesMe(userId, page, size));
    }

    @Operation(summary = "我喜欢的")
    @GetMapping("/i-like")
    public R<PageResult<Map<String, Object>>> getILike(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(likeService.getILike(userId, page, size));
    }

    @Operation(summary = "相互喜欢")
    @GetMapping("/mutual")
    public R<PageResult<Map<String, Object>>> getMutual(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(likeService.getMutual(userId, page, size));
    }

    @Operation(summary = "最新匹配")
    @GetMapping("/heartbeats")
    public R<List<Map<String, Object>>> getHeartbeats() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(likeService.getHeartbeats(userId));
    }
}
