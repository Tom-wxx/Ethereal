<template>
  <div class="chat-room">
    <EtherealNavBar :title="targetName" :show-back="true">
      <template #right><van-icon name="ellipsis" size="22" /></template>
    </EtherealNavBar>

    <div class="message-list" ref="messageListRef">
      <div v-for="msg in messages" :key="msg.id" class="message-item" :class="{ mine: msg.senderId === currentUserId }">
        <div class="message-bubble">{{ msg.content }}</div>
        <span class="message-time">{{ formatTime(msg.createdAt) }}</span>
      </div>
    </div>

    <div class="input-bar">
      <input v-model="inputText" placeholder="发送消息..." @keyup.enter="sendMessage" />
      <button class="send-btn" @click="sendMessage" :disabled="!inputText.trim()">
        <van-icon name="guide-o" size="20" color="#fff" />
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { formatTime } from '@/utils/format'
import type { ChatMessage } from '@/types/chat'

const route = useRoute()
const targetName = ref('聊天')
const inputText = ref('')
const currentUserId = 1
const messageListRef = ref<HTMLElement>()

const messages = ref<ChatMessage[]>([
  { id: 1, conversationId: 1, senderId: 2, msgType: 1, content: 'Hey! I noticed we both love jazz 🎵', isRead: 1, createdAt: new Date(Date.now() - 3600000).toISOString() },
  { id: 2, conversationId: 1, senderId: 1, msgType: 1, content: '是的！你最喜欢哪位爵士乐手？', isRead: 1, createdAt: new Date(Date.now() - 3500000).toISOString() },
  { id: 3, conversationId: 1, senderId: 2, msgType: 1, content: 'Miles Davis, definitely. Kind of Blue is my go-to album.', isRead: 1, createdAt: new Date(Date.now() - 3400000).toISOString() },
])

function sendMessage() {
  if (!inputText.value.trim()) return
  messages.value.push({
    id: Date.now(),
    conversationId: 1,
    senderId: currentUserId,
    msgType: 1,
    content: inputText.value,
    isRead: 0,
    createdAt: new Date().toISOString()
  })
  inputText.value = ''
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.chat-room {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: $surface;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.message-item {
  max-width: 75%;
  display: flex;
  flex-direction: column;

  &.mine {
    align-self: flex-end;
    .message-bubble {
      background: linear-gradient(135deg, $primary, $primary-container);
      color: #fff;
      border-radius: $radius-lg $radius-lg 4px $radius-lg;
    }
    .message-time { text-align: right; }
  }
}

.message-bubble {
  padding: 12px 16px;
  background: $surface-container-lowest;
  border-radius: $radius-lg $radius-lg $radius-lg 4px;
  font-size: 15px;
  line-height: 1.5;
  color: $on-surface;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.message-time {
  font-size: 11px;
  color: $outline;
  margin-top: 4px;
  padding: 0 4px;
}

.input-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(20px);
  border-top: 1px solid $surface-container-low;
  padding-bottom: max(12px, env(safe-area-inset-bottom));

  input {
    flex: 1;
    height: 40px;
    border: none;
    background: $surface-container-low;
    border-radius: $radius-full;
    padding: 0 16px;
    font-size: 15px;
    outline: none;

    &:focus { background: $surface-container-lowest; box-shadow: 0 0 0 1px rgba($primary, 0.2); }
  }

  .send-btn {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: none;
    background: linear-gradient(135deg, $primary, $primary-container);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;

    &:disabled { opacity: 0.4; }
  }
}
</style>
