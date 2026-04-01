package com.ethereal.module.chat.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ethereal.common.result.R;
import com.ethereal.module.chat.entity.ChatMessage;
import com.ethereal.module.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "消息模块")
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "获取会话列表")
    @GetMapping("/conversations")
    public R<List<Map<String, Object>>> getConversations() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.ok(chatService.getConversations(userId));
    }

    @Operation(summary = "获取消息记录")
    @GetMapping("/messages/{conversationId}")
    public R<List<ChatMessage>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "20") int size) {
        return R.ok(chatService.getMessages(conversationId, size));
    }

    @Operation(summary = "获取或创建会话")
    @PostMapping("/conversations")
    public R<Map<String, Object>> getOrCreateConversation(@RequestBody Map<String, Object> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        Long targetUserId = Long.parseLong(body.get("targetUserId").toString());
        var conv = chatService.getOrCreateConversation(userId, targetUserId);
        return R.ok(Map.of("id", conv.getId()));
    }

    @Operation(summary = "发送消息")
    @PostMapping("/messages")
    public R<ChatMessage> sendMessage(@RequestBody Map<String, Object> body) {
        Long userId = StpUtil.getLoginIdAsLong();
        Long conversationId = Long.parseLong(body.get("conversationId").toString());
        String content = (String) body.get("content");
        Integer msgType = body.get("msgType") != null ? Integer.parseInt(body.get("msgType").toString()) : 1;
        return R.ok(chatService.sendMessage(conversationId, userId, content, msgType));
    }

    @Operation(summary = "标记已读")
    @PostMapping("/conversations/{conversationId}/read")
    public R<Void> markRead(@PathVariable Long conversationId) {
        Long userId = StpUtil.getLoginIdAsLong();
        chatService.markRead(conversationId, userId);
        return R.ok();
    }
}
