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
public class AgentConciergeService {

    private final ChatLanguageModel chatModel;
    private final UserMapper userMapper;
    private final UserTagMapper userTagMapper;

    /**
     * AI管家对话
     */
    public Map<String, Object> chat(Long userId, String message, Long targetUserId) {
        User user = userMapper.selectById(userId);
        List<UserTag> userTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, userId));

        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("你是Ethereal的专属社交顾问，名叫Ethereal Concierge。");
        systemPrompt.append("你性格温暖、幽默、善解人意，擅长帮助用户拓展社交关系。");
        systemPrompt.append("请用简洁友好的中文回复。");
        systemPrompt.append("\n\n用户信息：");
        systemPrompt.append("\n- 昵称：").append(user != null ? user.getNickname() : "未知");
        systemPrompt.append("\n- 城市：").append(user != null ? user.getCity() : "未知");
        systemPrompt.append("\n- 兴趣：").append(userTags.stream().map(UserTag::getTagName).collect(Collectors.joining(", ")));

        if (targetUserId != null) {
            User target = userMapper.selectById(targetUserId);
            List<UserTag> targetTags = userTagMapper.selectList(
                    new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, targetUserId));
            if (target != null) {
                systemPrompt.append("\n\n对方信息：");
                systemPrompt.append("\n- 昵称：").append(target.getNickname());
                systemPrompt.append("\n- 城市：").append(target.getCity());
                systemPrompt.append("\n- 兴趣：").append(targetTags.stream().map(UserTag::getTagName).collect(Collectors.joining(", ")));
            }
        }

        try {
            String reply = chatModel.generate(systemPrompt + "\n\n用户说：" + message);
            Map<String, Object> result = new HashMap<>();
            result.put("reply", reply);
            return result;
        } catch (Exception e) {
            log.error("AI Concierge error", e);
            Map<String, Object> result = new HashMap<>();
            result.put("reply", "抱歉，我暂时无法回复。请稍后再试~");
            return result;
        }
    }

    /**
     * 生成破冰建议
     */
    public List<String> generateIceBreakers(Long userId, Long targetUserId) {
        User user = userMapper.selectById(userId);
        User target = userMapper.selectById(targetUserId);

        List<UserTag> userTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, userId));
        List<UserTag> targetTags = userTagMapper.selectList(
                new LambdaQueryWrapper<UserTag>().eq(UserTag::getUserId, targetUserId));

        // 找共同兴趣
        Set<String> commonTags = userTags.stream().map(UserTag::getTagName).collect(Collectors.toSet());
        commonTags.retainAll(targetTags.stream().map(UserTag::getTagName).collect(Collectors.toSet()));

        String prompt = String.format(
                "作为社交助手，请为以下两个用户生成3条自然、有趣的破冰开场白。\n\n" +
                "用户A：%s，兴趣：%s\n" +
                "用户B：%s，兴趣：%s\n" +
                "共同兴趣：%s\n\n" +
                "请直接返回3条开场白，每条一行，不要编号：",
                user != null ? user.getNickname() : "用户A",
                userTags.stream().map(UserTag::getTagName).collect(Collectors.joining(", ")),
                target != null ? target.getNickname() : "用户B",
                targetTags.stream().map(UserTag::getTagName).collect(Collectors.joining(", ")),
                String.join(", ", commonTags)
        );

        try {
            String response = chatModel.generate(prompt);
            return Arrays.stream(response.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(3)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Ice breaker generation error", e);
            return List.of("你好！看到你的资料很感兴趣~", "Hi！我们好像有很多共同爱好呢", "嗨，很高兴认识你！");
        }
    }
}
