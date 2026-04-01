package com.ethereal.module.moment.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ethereal.common.exception.BusinessException;
import com.ethereal.common.result.PageResult;
import com.ethereal.module.moment.entity.Moment;
import com.ethereal.module.moment.entity.MomentComment;
import com.ethereal.module.moment.entity.MomentLike;
import com.ethereal.module.moment.mapper.MomentCommentMapper;
import com.ethereal.module.moment.mapper.MomentLikeMapper;
import com.ethereal.module.moment.mapper.MomentMapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MomentService {

    private final MomentMapper momentMapper;
    private final MomentCommentMapper commentMapper;
    private final MomentLikeMapper likeMapper;
    private final UserMapper userMapper;

    public PageResult<Map<String, Object>> getNearby(Long userId, BigDecimal lng, BigDecimal lat, int page, int size) {
        // 简化：按时间倒序，后续可加地理位置排序
        return getMomentPage(userId, page, size, new LambdaQueryWrapper<Moment>()
                .eq(Moment::getStatus, 1)
                .eq(Moment::getAuditStatus, 1)
                .orderByDesc(Moment::getCreatedAt));
    }

    public PageResult<Map<String, Object>> getTrending(Long userId, int page, int size) {
        return getMomentPage(userId, page, size, new LambdaQueryWrapper<Moment>()
                .eq(Moment::getStatus, 1)
                .eq(Moment::getAuditStatus, 1)
                .orderByDesc(Moment::getLikeCount));
    }

    public PageResult<Map<String, Object>> getLatest(Long userId, int page, int size) {
        return getMomentPage(userId, page, size, new LambdaQueryWrapper<Moment>()
                .eq(Moment::getStatus, 1)
                .eq(Moment::getAuditStatus, 1)
                .orderByDesc(Moment::getCreatedAt));
    }

    @Transactional
    public Long createMoment(Long userId, String content, List<String> images, BigDecimal longitude, BigDecimal latitude, String locationName) {
        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setContent(content);
        moment.setImages(images);
        moment.setLongitude(longitude);
        moment.setLatitude(latitude);
        moment.setLocationName(locationName);
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        moment.setAuditStatus(1); // 自动通过，后续可改为0待审核
        moment.setStatus(1);
        momentMapper.insert(moment);
        return moment.getId();
    }

    @Transactional
    public void likeMoment(Long momentId, Long userId) {
        MomentLike existing = likeMapper.selectOne(
                new LambdaQueryWrapper<MomentLike>()
                        .eq(MomentLike::getMomentId, momentId)
                        .eq(MomentLike::getUserId, userId));
        if (existing != null) return;

        MomentLike like = new MomentLike();
        like.setMomentId(momentId);
        like.setUserId(userId);
        likeMapper.insert(like);

        Moment moment = momentMapper.selectById(momentId);
        if (moment != null) {
            moment.setLikeCount(moment.getLikeCount() + 1);
            momentMapper.updateById(moment);
        }
    }

    @Transactional
    public void unlikeMoment(Long momentId, Long userId) {
        likeMapper.delete(new LambdaQueryWrapper<MomentLike>()
                .eq(MomentLike::getMomentId, momentId)
                .eq(MomentLike::getUserId, userId));

        Moment moment = momentMapper.selectById(momentId);
        if (moment != null && moment.getLikeCount() > 0) {
            moment.setLikeCount(moment.getLikeCount() - 1);
            momentMapper.updateById(moment);
        }
    }

    public PageResult<Map<String, Object>> getComments(Long momentId, int page, int size) {
        Page<MomentComment> p = commentMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<MomentComment>()
                        .eq(MomentComment::getMomentId, momentId)
                        .orderByAsc(MomentComment::getCreatedAt));

        PageResult<Map<String, Object>> result = new PageResult<>();
        result.setTotal(p.getTotal());
        result.setCurrent(p.getCurrent());
        result.setSize(p.getSize());
        result.setRecords(p.getRecords().stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("content", c.getContent());
            map.put("parentId", c.getParentId());
            map.put("createdAt", c.getCreatedAt());
            User user = userMapper.selectById(c.getUserId());
            if (user != null) {
                map.put("userId", user.getId());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }
            return map;
        }).collect(Collectors.toList()));
        return result;
    }

    @Transactional
    public void addComment(Long momentId, Long userId, String content, Long parentId) {
        MomentComment comment = new MomentComment();
        comment.setMomentId(momentId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setParentId(parentId != null ? parentId : 0L);
        commentMapper.insert(comment);

        Moment moment = momentMapper.selectById(momentId);
        if (moment != null) {
            moment.setCommentCount(moment.getCommentCount() + 1);
            momentMapper.updateById(moment);
        }
    }

    @Transactional
    public void deleteMoment(Long momentId, Long userId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null || !moment.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权删除");
        }
        moment.setStatus(2);
        momentMapper.updateById(moment);
    }

    private PageResult<Map<String, Object>> getMomentPage(Long userId, int page, int size, LambdaQueryWrapper<Moment> wrapper) {
        Page<Moment> p = momentMapper.selectPage(new Page<>(page, size), wrapper);

        PageResult<Map<String, Object>> result = new PageResult<>();
        result.setTotal(p.getTotal());
        result.setCurrent(p.getCurrent());
        result.setSize(p.getSize());

        result.setRecords(p.getRecords().stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("content", m.getContent());
            map.put("images", m.getImages());
            map.put("locationName", m.getLocationName());
            map.put("likeCount", m.getLikeCount());
            map.put("commentCount", m.getCommentCount());
            map.put("createdAt", m.getCreatedAt());

            User user = userMapper.selectById(m.getUserId());
            if (user != null) {
                map.put("userId", user.getId());
                map.put("nickname", user.getNickname());
                map.put("avatar", user.getAvatar());
            }

            // 检查当前用户是否已点赞
            MomentLike liked = likeMapper.selectOne(
                    new LambdaQueryWrapper<MomentLike>()
                            .eq(MomentLike::getMomentId, m.getId())
                            .eq(MomentLike::getUserId, userId));
            map.put("isLiked", liked != null);
            return map;
        }).collect(Collectors.toList()));

        return result;
    }
}
