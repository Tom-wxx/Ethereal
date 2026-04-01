package com.ethereal.module.like.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ethereal.common.constant.RedisKeyConstant;
import com.ethereal.common.result.PageResult;
import com.ethereal.module.like.entity.UserLike;
import com.ethereal.module.like.mapper.UserLikeMapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserLikeMapper userLikeMapper;
    private final UserMapper userMapper;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public boolean doAction(Long userId, Long targetUserId, String action) {
        // 记录到已浏览
        String seenKey = RedisKeyConstant.DISCOVERY_SEEN + userId;
        redisTemplate.opsForSet().add(seenKey, targetUserId.toString());
        redisTemplate.expire(seenKey, 30, TimeUnit.DAYS);

        int actionType;
        switch (action) {
            case "like": actionType = 1; break;
            case "super_like": actionType = 2; break;
            default: actionType = 3; break; // pass
        }

        // 跳过不记录喜欢
        if (actionType == 3) return false;

        // 检查是否已存在
        UserLike existing = userLikeMapper.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getTargetUserId, targetUserId));
        if (existing != null) {
            existing.setActionType(actionType);
            userLikeMapper.updateById(existing);
        } else {
            UserLike like = new UserLike();
            like.setUserId(userId);
            like.setTargetUserId(targetUserId);
            like.setActionType(actionType);
            like.setIsMutual(0);
            userLikeMapper.insert(like);
        }

        // 检查是否互相喜欢
        UserLike reverse = userLikeMapper.selectOne(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, targetUserId)
                        .eq(UserLike::getTargetUserId, userId)
                        .in(UserLike::getActionType, 1, 2));

        if (reverse != null) {
            // 双向标记互相喜欢
            userLikeMapper.update(null, new LambdaUpdateWrapper<UserLike>()
                    .eq(UserLike::getUserId, userId)
                    .eq(UserLike::getTargetUserId, targetUserId)
                    .set(UserLike::getIsMutual, 1));
            reverse.setIsMutual(1);
            userLikeMapper.updateById(reverse);
            return true; // 互相喜欢！
        }

        return false;
    }

    public PageResult<Map<String, Object>> getWhoLikesMe(Long userId, int page, int size) {
        Page<UserLike> p = userLikeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getTargetUserId, userId)
                        .in(UserLike::getActionType, 1, 2)
                        .orderByDesc(UserLike::getCreatedAt));
        return buildLikePageResult(p, true);
    }

    public PageResult<Map<String, Object>> getILike(Long userId, int page, int size) {
        Page<UserLike> p = userLikeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .in(UserLike::getActionType, 1, 2)
                        .orderByDesc(UserLike::getCreatedAt));
        return buildLikePageResult(p, false);
    }

    public PageResult<Map<String, Object>> getMutual(Long userId, int page, int size) {
        Page<UserLike> p = userLikeMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getUserId, userId)
                        .eq(UserLike::getIsMutual, 1)
                        .orderByDesc(UserLike::getCreatedAt));
        return buildLikePageResult(p, false);
    }

    public List<Map<String, Object>> getHeartbeats(Long userId) {
        List<UserLike> recent = userLikeMapper.selectList(
                new LambdaQueryWrapper<UserLike>()
                        .eq(UserLike::getTargetUserId, userId)
                        .eq(UserLike::getIsMutual, 1)
                        .orderByDesc(UserLike::getCreatedAt)
                        .last("LIMIT 10"));

        return recent.stream().map(like -> {
            User user = userMapper.selectById(like.getUserId());
            Map<String, Object> map = new HashMap<>();
            if (user != null) {
                map.put("id", user.getId());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
                map.put("matchScore", 90 + new Random().nextInt(10)); // 临时mock
            }
            return map;
        }).collect(Collectors.toList());
    }

    private PageResult<Map<String, Object>> buildLikePageResult(Page<UserLike> p, boolean useUserId) {
        PageResult<Map<String, Object>> result = new PageResult<>();
        result.setTotal(p.getTotal());
        result.setCurrent(p.getCurrent());
        result.setSize(p.getSize());

        List<Map<String, Object>> records = p.getRecords().stream().map(like -> {
            Long targetId = useUserId ? like.getUserId() : like.getTargetUserId();
            User user = userMapper.selectById(targetId);
            Map<String, Object> map = new HashMap<>();
            if (user != null) {
                map.put("id", user.getId());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
                map.put("profession", user.getProfession());
                map.put("city", user.getCity());
                map.put("createdAt", like.getCreatedAt());
                if (user.getBirthday() != null) {
                    map.put("age", Period.between(user.getBirthday(), LocalDate.now()).getYears());
                }
                map.put("matchScore", 80 + new Random().nextInt(20));
            }
            return map;
        }).collect(Collectors.toList());

        result.setRecords(records);
        return result;
    }
}
