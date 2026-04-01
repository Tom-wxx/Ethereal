package com.ethereal.module.chat.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ethereal.module.chat.entity.ChatMessage;
import com.ethereal.module.chat.service.ChatService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSocketHandler {

    private final SocketIOServer socketIOServer;
    private final ChatService chatService;

    // 用户ID -> SocketIO客户端
    private static final Map<Long, SocketIOClient> onlineUsers = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        socketIOServer.addConnectListener(this::onConnect);
        socketIOServer.addDisconnectListener(this::onDisconnect);
        socketIOServer.addEventListener("chat:send", Map.class, (client, data, ack) -> onChatSend(client, data));
    }

    public void onConnect(SocketIOClient client) {
        String userIdStr = client.getHandshakeData().getSingleUrlParam("userId");
        if (userIdStr != null) {
            Long userId = Long.parseLong(userIdStr);
            onlineUsers.put(userId, client);
            client.set("userId", userId);
            log.info("User {} connected", userId);
        }
    }

    public void onDisconnect(SocketIOClient client) {
        Long userId = client.get("userId");
        if (userId != null) {
            onlineUsers.remove(userId);
            log.info("User {} disconnected", userId);
        }
    }

    public void onChatSend(SocketIOClient client, Map data) {
        Long senderId = client.get("userId");
        if (senderId == null) return;

        Long conversationId = Long.parseLong(data.get("conversationId").toString());
        String content = (String) data.get("content");
        Integer msgType = data.get("msgType") != null ? Integer.parseInt(data.get("msgType").toString()) : 1;

        ChatMessage msg = chatService.sendMessage(conversationId, senderId, content, msgType);

        Map<String, Object> msgData = Map.of(
                "id", msg.getId(),
                "conversationId", msg.getConversationId(),
                "senderId", msg.getSenderId(),
                "content", msg.getContent(),
                "msgType", msg.getMsgType(),
                "createdAt", msg.getCreatedAt().toString()
        );

        // 找到会话对方，精准推送
        var conv = chatService.getConversationById(conversationId);
        if (conv != null) {
            Long receiverId = conv.getUserIdA().equals(senderId) ? conv.getUserIdB() : conv.getUserIdA();
            SocketIOClient receiverClient = onlineUsers.get(receiverId);
            if (receiverClient != null && receiverClient.isChannelOpen()) {
                receiverClient.sendEvent("chat:receive", msgData);
            }
        }
    }

    public static void sendToUser(Long userId, String event, Object data) {
        SocketIOClient client = onlineUsers.get(userId);
        if (client != null && client.isChannelOpen()) {
            client.sendEvent(event, data);
        }
    }
}
