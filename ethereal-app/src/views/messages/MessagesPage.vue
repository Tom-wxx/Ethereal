<template>
  <div class="messages-page">
    <EtherealNavBar>
      <template #title><span class="brand-title">Ethereal</span></template>
      <template #right><van-icon name="setting-o" size="22" color="#595c5d" /></template>
    </EtherealNavBar>

    <!-- 新匹配 -->
    <div class="matches-section">
      <div class="section-header">
        <h3>新匹配</h3>
        <span class="badge">{{ matches.length }} New</span>
      </div>
      <div class="match-scroll">
        <div v-for="m in matches" :key="m.id" class="match-card" @click="goChat(m.id)">
          <img :src="m.avatar || 'https://picsum.photos/60/60?random=' + m.id" class="match-avatar" />
          <span class="match-name">{{ m.nickname }}</span>
        </div>
        <div class="match-card boost-card">
          <van-icon name="fire-o" size="24" color="#FF9F1C" />
          <span>Boost</span>
        </div>
      </div>
    </div>

    <!-- 聊天列表 -->
    <div class="chat-list">
      <h3 class="list-title">最近聊天</h3>
      <div v-for="conv in conversations" :key="conv.id" class="chat-item" @click="goChat(conv.targetUser.id)">
        <div class="avatar-wrapper">
          <img :src="conv.targetUser.avatar || 'https://picsum.photos/48/48?random=' + conv.id" class="chat-avatar" />
          <div v-if="conv.unreadCount > 0" class="unread-dot">{{ conv.unreadCount }}</div>
        </div>
        <div class="chat-info">
          <div class="chat-top">
            <span class="chat-name">{{ conv.targetUser.nickname }}</span>
            <span class="chat-time">{{ formatTime(conv.lastMsgTime) }}</span>
          </div>
          <p class="chat-msg">{{ conv.lastMsgContent }}</p>
        </div>
      </div>

      <!-- Concierge -->
      <div class="chat-item concierge" @click="goChat(0)">
        <div class="avatar-wrapper">
          <div class="concierge-avatar">
            <van-icon name="service-o" size="22" color="#FF4D6D" />
          </div>
        </div>
        <div class="chat-info">
          <div class="chat-top">
            <span class="chat-name">Ethereal Concierge</span>
            <span class="chat-time">3天前</span>
          </div>
          <p class="chat-msg">You have 12 new people interested in your profile!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { formatTime } from '@/utils/format'
import type { Conversation } from '@/types/chat'

const router = useRouter()

const matches = ref([
  { id: 1, nickname: 'Julian', avatar: '' },
  { id: 2, nickname: 'Sienna', avatar: '' },
  { id: 3, nickname: 'Kai', avatar: '' },
  { id: 4, nickname: 'Amara', avatar: '' },
])

const conversations = ref<Conversation[]>([
  { id: 1, targetUser: { id: 5, nickname: 'Leo', avatar: '' }, lastMsgContent: 'I really loved that gallery you shared!', lastMsgTime: new Date(Date.now() - 120000).toISOString(), unreadCount: 2 },
  { id: 2, targetUser: { id: 6, nickname: 'Evelyn', avatar: '' }, lastMsgContent: 'Maybe we could grab coffee this weekend?', lastMsgTime: new Date(Date.now() - 3600000).toISOString(), unreadCount: 1 },
  { id: 3, targetUser: { id: 7, nickname: 'Marcus', avatar: '' }, lastMsgContent: "That's such a great point, hadn't thought about it.", lastMsgTime: new Date(Date.now() - 86400000).toISOString(), unreadCount: 0 },
  { id: 4, targetUser: { id: 8, nickname: 'Chloe', avatar: '' }, lastMsgContent: 'The book you recommended was incredible!', lastMsgTime: new Date(Date.now() - 172800000).toISOString(), unreadCount: 0 },
])

function goChat(userId: number) {
  router.push(`/chat/${userId}`)
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.messages-page {
  min-height: 100vh;
  background: $surface;
  padding-bottom: 80px;
}

.brand-title {
  font-family: 'Plus Jakarta Sans', sans-serif;
  font-weight: 800;
  background: linear-gradient(135deg, $primary, $secondary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.matches-section {
  padding: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;

  h3 { margin: 0; font-size: 18px; font-weight: 700; font-family: 'Plus Jakarta Sans', sans-serif; }
  .badge { background: $primary; color: #fff; font-size: 11px; padding: 2px 8px; border-radius: $radius-full; font-weight: 600; }
}

.match-scroll {
  display: flex;
  gap: 14px;
  overflow-x: auto;
  padding-bottom: 8px;
  &::-webkit-scrollbar { display: none; }
}

.match-card {
  text-align: center;
  cursor: pointer;

  .match-avatar {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid $primary;
    padding: 2px;
  }

  .match-name {
    display: block;
    font-size: 12px;
    margin-top: 6px;
    color: $on-surface;
    font-weight: 500;
  }
}

.boost-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 56px;

  span { font-size: 11px; color: $secondary; font-weight: 600; margin-top: 4px; }
}

.chat-list { padding: 0 16px; }

.list-title {
  font-size: 16px;
  font-weight: 700;
  margin: 8px 0 12px;
  font-family: 'Plus Jakarta Sans', sans-serif;
}

.chat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  cursor: pointer;

  &:not(:last-child) { border-bottom: 1px solid $surface-container-low; }
}

.avatar-wrapper { position: relative; }

.chat-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.unread-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  background: $primary;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.chat-info { flex: 1; overflow: hidden; }

.chat-top {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .chat-name { font-weight: 600; font-size: 15px; color: $on-surface; }
  .chat-time { font-size: 12px; color: $outline; }
}

.chat-msg {
  margin: 2px 0 0;
  font-size: 14px;
  color: $on-surface-variant;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.concierge-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba($primary, 0.1), rgba($secondary, 0.1));
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
