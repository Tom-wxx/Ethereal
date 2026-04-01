package com.ethereal.module.moment.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ethereal.common.result.PageResult;
import com.ethereal.common.result.R;
import com.ethereal.module.moment.service.MomentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "动态模块")
@RestController
@RequestMapping("/api/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @Operation(summary = "附近动态")
    @GetMapping("/nearby")
    public R<PageResult<Map<String, Object>>> getNearby(
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(momentService.getNearby(userId, longitude, latitude, page, size));
    }

    @Operation(summary = "热门动态")
    @GetMapping("/trending")
    public R<PageResult<Map<String, Object>>> getTrending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(momentService.getTrending(userId, page, size));
    }

    @Operation(summary = "最新动态")
    @GetMapping("/latest")
    public R<PageResult<Map<String, Object>>> getLatest(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(momentService.getLatest(userId, page, size));
    }

    @Operation(summary = "发布动态")
    @PostMapping
    public R<Long> createMoment(@RequestBody Map<String, Object> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        String content = (String) body.get("content");
        List<String> images = (List<String>) body.get("images");
        BigDecimal lng = body.get("longitude") != null ? new BigDecimal(body.get("longitude").toString()) : null;
        BigDecimal lat = body.get("latitude") != null ? new BigDecimal(body.get("latitude").toString()) : null;
        String locationName = (String) body.get("locationName");
        return R.ok(momentService.createMoment(userId, content, images, lng, lat, locationName));
    }

    @Operation(summary = "删除动态")
    @DeleteMapping("/{id}")
    public R<Void> deleteMoment(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        momentService.deleteMoment(id, userId);
        return R.ok();
    }

    @Operation(summary = "点赞动态")
    @PostMapping("/{id}/like")
    public R<Void> likeMoment(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        momentService.likeMoment(id, userId);
        return R.ok();
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{id}/like")
    public R<Void> unlikeMoment(@PathVariable Long id) {
        Long userId = StpUtil.getLoginIdAsLong();
        momentService.unlikeMoment(id, userId);
        return R.ok();
    }

    @Operation(summary = "评论列表")
    @GetMapping("/{id}/comments")
    public R<PageResult<Map<String, Object>>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(momentService.getComments(id, page, size));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/{id}/comments")
    public R<Void> addComment(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        String content = (String) body.get("content");
        Long parentId = body.get("parentId") != null ? Long.parseLong(body.get("parentId").toString()) : null;
        momentService.addComment(id, userId, content, parentId);
        return R.ok();
    }
}
