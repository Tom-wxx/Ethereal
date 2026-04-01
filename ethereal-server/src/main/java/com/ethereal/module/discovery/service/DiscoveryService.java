package com.ethereal.module.discovery.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ethereal.common.constant.RedisKeyConstant;
import com.ethereal.module.discovery.dto.DiscoveryCardVO;
import com.ethereal.module.like.entity.UserLike;
import com.ethereal.module.like.mapper.UserLikeMapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.entity.UserPhoto;
import com.ethereal.module.user.entity.UserTag;
import com.ethereal.module.user.mapper.UserMapper;
import com.ethereal.module.user.mapper.UserPhotoMapper;
import com.ethereal.module.user.mapper.UserTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscoveryService {

    private final UserMapper userMapper;
    private final UserTagMapper userTagMapper;
    private final UserPhotoMapper userPhotoMapper;
    private final UserLikeMapper userLikeMapper;
    private final StringRedisTemplate redisTemplate;

    public List<DiscoveryCardVO> getCards(Long userId, Integer gender, Integer ageMin, Integer ageMax, Double distance) {
        // 获取已浏览的用户ID集合
        String seenKey = RedisKeyConstant.DISCOVERY_SEEN + userId;
        Set<String> seenIds = redisTemplate.opsForSet().members(seenKey);
        Set<Long> seenUserIds = seenIds != null ? seenIds.stream().map(Long::parseLong).collect(Collectors.toSet()) : new HashSet<>();
        seenUserIds.add(userId); // 排除自己

        // 查询候选用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .eq(User::getStatus, 1)
                .notIn(User::getId, seenUserIds);

        if (gender != null) wrapper.eq(User::getGender, gender);

        List<User> candidates = userMapper.selectList(wrapper.last("LIMIT 50"));

        // 按基础规则排序（后续可替换为Agent推荐）
        List<DiscoveryCardVO> cards = new ArrayList<>();
        for (User u : candidates) {
            DiscoveryCardVO card = new DiscoveryCardVO();
            card.setId(u.getId());
            card.setNickname(u.getNickname());
            card.setAvatar(u.getAvatar());
            card.setGender(u.getGender());
            card.setProfession(u.getProfession());
            card.setCity(u.getCity());
            card.setVerified(u.getVerified());
            card.setVibeScore(u.getVibeScore());
            card.setBio(u.getBio());

            if (u.getBirthday() != null) {
                card.setAge(Period.between(u.getBirthday(), LocalDate.now()).getYears());
            }

            // 加载标签
            List<UserTag> tags = userTagMapper.selectList(
                    new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, u.getId()));
            card.setTags(tags.stream().map(UserTag::getTagName).collect(Collectors.toList()));

            // 加载照片
            List<UserPhoto> photos = userPhotoMapper.selectList(
                    new LambdaQueryWrapper<UserPhoto>()
                            .eq(UserPhoto::getUserId, u.getId())
                            .eq(UserPhoto::getAuditStatus, 1));
            card.setPhotos(photos.stream().map(UserPhoto::getPhotoUrl).collect(Collectors.toList()));

            cards.add(card);
        }

        // 返回前10张卡片
        return cards.stream().limit(10).collect(Collectors.toList());
    }
}
