package com.ethereal.module.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.entity.UserTag;
import com.ethereal.module.user.mapper.UserMapper;
import com.ethereal.module.user.mapper.UserTagMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentRecommendService {

    private final ChatLanguageModel chatModel;
    private final UserMapper userMapper;
    private final UserTagMapper userTagMapper;

    /**
     * AI增强的推荐排序
     * 基于用户画像的标签匹配 + AI评分
     */
    public List<Long> getAIRecommendedUserIds(Long userId, List<Long> candidateIds, int count) {
        if (candidateIds.isEmpty()) return Collections.emptyList();

        User currentUser = userMapper.selectById(userId);
        List<UserTag> currentTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, userId));
        Set<String> currentTagNames = currentTags.stream().map(UserTag::getTagName).collect(Collectors.toSet());

        // 基于标签交集打分
        List<Map.Entry<Long, Double>> scored = new ArrayList<>();
        for (Long candidateId : candidateIds) {
            List<UserTag> candidateTags = userTagMapper.selectList(
                    new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, candidateId));
            Set<String> candidateTagNames = candidateTags.stream().map(UserTag::getTagName).collect(Collectors.toSet());

            // 计算Jaccard相似度
            Set<String> intersection = new HashSet<>(currentTagNames);
            intersection.retainAll(candidateTagNames);
            Set<String> union = new HashSet<>(currentTagNames);
            union.addAll(candidateTagNames);

            double similarity = union.isEmpty() ? 0 : (double) intersection.size() / union.size();

            // 加上魅力值权重
            User candidate = userMapper.selectById(candidateId);
            double vibeBonus = candidate != null && candidate.getVibeScore() != null ? candidate.getVibeScore() / 100.0 * 0.2 : 0;

            // 加上随机因子确保多样性
            double randomFactor = Math.random() * 0.1;

            double score = similarity * 0.5 + vibeBonus + randomFactor;
            scored.add(Map.entry(candidateId, score));
        }

        // 按分数降序取Top N
        return scored.stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 使用AI分析用户匹配度（可选的深度分析）
     */
    public String analyzeCompatibility(Long userId, Long targetUserId) {
        User user = userMapper.selectById(userId);
        User target = userMapper.selectById(targetUserId);

        List<UserTag> userTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, userId));
        List<UserTag> targetTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, targetUserId));

        String prompt = String.format(
                "分析以下两位用户的匹配度，给出简短评价（50字以内）。\n" +
                "用户A：%s，%s，兴趣：%s\n" +
                "用户B：%s，%s，兴趣：%s",
                user != null ? user.getNickname() : "A",
                user != null ? user.getProfession() : "",
                userTags.stream().map(UserTag::getTagName).collect(Collectors.joining(", ")),
                target != null ? target.getNickname() : "B",
                target != null ? target.getProfession() : "",
                targetTags.stream().map(UserTag::getTagName).collect(Collectors.joining(", "))
        );

        try {
            return chatModel.generate(prompt);
        } catch (Exception e) {
            log.error("Compatibility analysis error", e);
            return "你们有不少共同点呢！";
        }
    }
}
