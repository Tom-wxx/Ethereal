package com.ethereal.module.chat.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ethereal.module.chat.entity.ChatMessage;
import com.ethereal.module.chat.entity.Conversation;
import com.ethereal.module.chat.mapper.ChatMessageMapper;
import com.ethereal.module.chat.mapper.ConversationMapper;
import com.ethereal.module.user.entity.User;
import com.ethereal.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationMapper conversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;

    public List<Map<String, Object>> getConversations(Long userId) {
        List<Conversation> convs = conversationMapper.selectList(
                new LambdaQueryWrapper<Conversation>()
                        .and(w -> w.eq(Conversation::getUserIdA, userId).or().eq(Conversation::getUserIdB, userId))
                        .eq(Conversation::getStatus, 1)
                        .orderByDesc(Conversation::getLastMsgTime));

        return convs.stream().map(conv -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", conv.getId());
            map.put("lastMsgContent", conv.getLastMsgContent());
            map.put("lastMsgTime", conv.getLastMsgTime());

            Long targetId = conv.getUserIdA().equals(userId) ? conv.getUserIdB() : conv.getUserIdA();
            int unread = conv.getUserIdA().equals(userId) ? conv.getUnreadCountA() : conv.getUnreadCountB();
            map.put("unreadCount", unread);

            User target = userMapper.selectById(targetId);
            if (target != null) {
                Map<String, Object> targetUser = new HashMap<>();
                targetUser.put("id", target.getId());
                targetUser.put("nickname", target.getNickname());
                targetUser.put("avatar", target.getAvatar());
                map.put("targetUser", targetUser);
            }
            return map;
        }).collect(Collectors.toList());
    }

    public List<ChatMessage> getMessages(Long conversationId, int size) {
        Page<ChatMessage> page = chatMessageMapper.selectPage(new Page<>(1, size),
                new LambdaQueryWrapper<ChatMessage>()
                        .eq(ChatMessage::getConversationId, conversationId)
                        .orderByDesc(ChatMessage::getCreatedAt));
        List<ChatMessage> messages = page.getRecords();
        Collections.reverse(messages);
        return messages;
    }

    @Transactional
    public ChatMessage sendMessage(Long conversationId, Long senderId, String content, Integer msgType) {
        ChatMessage msg = new ChatMessage();
        msg.setConversationId(conversationId);
        msg.setSenderId(senderId);
        msg.setContent(content);
        msg.setMsgType(msgType != null ? msgType : 1);
        msg.setIsRead(0);
        chatMessageMapper.insert(msg);

        // 更新会话最后消息
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv != null) {
            conv.setLastMsgContent(content);
            conv.setLastMsgTime(LocalDateTime.now());
            // 对方未读+1
            if (conv.getUserIdA().equals(senderId)) {
                conv.setUnreadCountB(conv.getUnreadCountB() + 1);
            } else {
                conv.setUnreadCountA(conv.getUnreadCountA() + 1);
            }
            conversationMapper.updateById(conv);
        }

        return msg;
    }

    @Transactional
    public void markRead(Long conversationId, Long userId) {
        Conversation conv = conversationMapper.selectById(conversationId);
        if (conv != null) {
            if (conv.getUserIdA().equals(userId)) {
                conv.setUnreadCountA(0);
            } else {
                conv.setUnreadCountB(0);
            }
            conversationMapper.updateById(conv);

            // 标记消息已读
            chatMessageMapper.update(null, new LambdaUpdateWrapper<ChatMessage>()
                    .eq(ChatMessage::getConversationId, conversationId)
                    .ne(ChatMessage::getSenderId, userId)
                    .eq(ChatMessage::getIsRead, 0)
                    .set(ChatMessage::getIsRead, 1));
        }
    }

    public Conversation getConversationById(Long conversationId) {
        return conversationMapper.selectById(conversationId);
    }

    public Conversation getOrCreateConversation(Long userIdA, Long userIdB) {
        Long minId = Math.min(userIdA, userIdB);
        Long maxId = Math.max(userIdA, userIdB);

        Conversation conv = conversationMapper.selectOne(
                new LambdaQueryWrapper<Conversation>()
                        .eq(Conversation::getUserIdA, minId)
                        .eq(Conversation::getUserIdB, maxId));

        if (conv == null) {
            conv = new Conversation();
            conv.setUserIdA(minId);
            conv.setUserIdB(maxId);
            conv.setStatus(1);
            conv.setUnreadCountA(0);
            conv.setUnreadCountB(0);
            conversationMapper.insert(conv);
        }
        return conv;
    }
}
