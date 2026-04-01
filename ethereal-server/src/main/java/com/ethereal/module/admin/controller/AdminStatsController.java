package com.ethereal.module.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ethereal.common.result.R;
import com.ethereal.module.like.mapper.UserLikeMapper;
import com.ethereal.module.like.entity.UserLike;
import com.ethereal.module.moment.mapper.MomentMapper;
import com.ethereal.module.moment.entity.Moment;
import com.ethereal.module.user.mapper.UserMapper;
import com.ethereal.module.user.mapper.UserPhotoMapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.entity.UserPhoto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Tag(name = "管理后台-数据统计")
@RestController
@RequestMapping("/admin-api/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final UserMapper userMapper;
    private final UserLikeMapper userLikeMapper;
    private final MomentMapper momentMapper;
    private final UserPhotoMapper userPhotoMapper;

    @Operation(summary = "数据概览")
    @GetMapping("/overview")
    public R<Map<String, Object>> getOverview() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime yesterdayStart = todayStart.minusDays(1);

        Long todayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreatedAt, todayStart));
        Long yesterdayNewUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .ge(User::getCreatedAt, yesterdayStart)
                        .lt(User::getCreatedAt, todayStart));

        Long totalUsers = userMapper.selectCount(null);

        Long todayMatches = userLikeMapper.selectCount(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getIsMutual, 1)
                        .ge(UserLike::getCreatedAt, todayStart));
        Long yesterdayMatches = userLikeMapper.selectCount(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getIsMutual, 1)
                        .ge(UserLike::getCreatedAt, yesterdayStart)
                        .lt(UserLike::getCreatedAt, todayStart));

        Long pendingMomentAudit = momentMapper.selectCount(
                new LambdaQueryWrapper<Moment>().eq(Moment::getAuditStatus, 0));
        Long pendingPhotoAudit = userPhotoMapper.selectCount(
                new LambdaQueryWrapper<UserPhoto>().eq(UserPhoto::getAuditStatus, 0));
        long pendingAudit = pendingMomentAudit + pendingPhotoAudit;

        Long todayActiveUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getLastLoginTime, todayStart));
        Long yesterdayActiveUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .ge(User::getLastLoginTime, yesterdayStart)
                        .lt(User::getLastLoginTime, todayStart));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("todayNewUsers", todayNewUsers);
        result.put("todayNewUsersTrend", calcTrend(todayNewUsers, yesterdayNewUsers));
        result.put("totalUsers", totalUsers);
        result.put("todayActiveUsers", todayActiveUsers);
        result.put("todayActiveUsersTrend", calcTrend(todayActiveUsers, yesterdayActiveUsers));
        result.put("todayMatches", todayMatches);
        result.put("todayMatchesTrend", calcTrend(todayMatches, yesterdayMatches));
        result.put("pendingAudit", pendingAudit);
        return R.ok(result);
    }

    @Operation(summary = "用户增长趋势（最近14天）")
    @GetMapping("/growth")
    public R<List<Map<String, Object>>> getGrowth() {
        List<Map<String, Object>> data = new ArrayList<>();
        for (int i = 13; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            Long count = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .ge(User::getCreatedAt, date.atStartOfDay())
                            .lt(User::getCreatedAt, date.plusDays(1).atStartOfDay()));
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("date", date.toString());
            point.put("count", count);
            data.add(point);
        }
        return R.ok(data);
    }

    @Operation(summary = "最近注册用户")
    @GetMapping("/recent-users")
    public R<List<Map<String, Object>>> getRecentUsers(
            @RequestParam(defaultValue = "5") int limit) {
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>()
                        .orderByDesc(User::getCreatedAt)
                        .last("LIMIT " + Math.min(limit, 20)));

        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", u.getId());
            item.put("nickname", u.getNickname());
            item.put("avatar", u.getAvatar());
            item.put("phone", u.getPhone());
            item.put("city", u.getCity());
            item.put("createdAt", u.getCreatedAt());
            result.add(item);
        }
        return R.ok(result);
    }

    private double calcTrend(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current != null && current > 0 ? 100.0 : 0.0;
        }
        return Math.round((current - previous) * 1000.0 / previous) / 10.0;
    }
}
