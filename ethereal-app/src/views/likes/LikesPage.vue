<template>
  <div class="likes-page">
    <EtherealNavBar>
      <template #title><span class="brand-title">Ethereal</span></template>
      <template #right><van-icon name="setting-o" size="22" color="#595c5d" /></template>
    </EtherealNavBar>

    <van-tabs v-model:active="activeTab" color="#FF4D6D" line-width="24" shrink>
      <van-tab title="谁喜欢我" />
      <van-tab title="我喜欢的" />
      <van-tab title="相互喜欢" />
    </van-tabs>

    <!-- Discovery Heartbeats -->
    <div class="heartbeats-section">
      <div class="section-header">
        <h3>Discovery Heartbeats</h3>
        <span class="badge">{{ heartbeats.length }} New</span>
      </div>
      <div class="heartbeat-scroll">
        <div v-for="user in heartbeats" :key="user.id" class="heartbeat-card">
          <img :src="user.avatar || 'https://picsum.photos/100/130?random=' + user.id" class="heartbeat-avatar" />
          <span class="heartbeat-name">{{ user.nickname }}</span>
          <span class="heartbeat-score">{{ user.matchScore }}%</span>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="user-list">
      <div v-for="user in likeUsers" :key="user.id" class="like-user-card">
        <img :src="user.avatar || 'https://picsum.photos/80/80?random=' + user.id" class="user-avatar" />
        <div class="user-info">
          <h4>{{ user.nickname }} <span class="age">{{ user.age }}</span></h4>
          <p class="profession">{{ user.profession }}</p>
          <p class="location">{{ user.city }}</p>
        </div>
        <div class="match-score">
          <span>{{ user.matchScore }}%</span>
          <van-icon name="like" color="#FF4D6D" size="20" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { getWhoLikesMe, getILike, getMutual, getHeartbeats } from '@/api/like'
import type { LikeUser } from '@/types/user'

const activeTab = ref(0)
const likeUsers = ref<LikeUser[]>([])
const heartbeats = ref<LikeUser[]>([])

const mockUsers: LikeUser[] = [
  { id: 1, nickname: 'Lina', avatar: '', age: 24, profession: '创意总监', city: 'Shanghai', matchScore: 98, createdAt: '' },
  { id: 2, nickname: '真', avatar: '', age: 27, profession: '建筑师', city: 'Shanghai', matchScore: 95, createdAt: '' },
  { id: 3, nickname: 'Mei', avatar: '', age: 22, profession: 'UX设计师', city: 'Shanghai', matchScore: 92, createdAt: '' },
  { id: 4, nickname: 'Chloe', avatar: '', age: 25, profession: '画廊主', city: 'Shanghai', matchScore: 89, createdAt: '' },
]

async function loadData() {
  try {
    const fetchers = [getWhoLikesMe, getILike, getMutual]
    const data = await fetchers[activeTab.value]() as unknown as any
    likeUsers.value = data?.records || mockUsers
  } catch {
    likeUsers.value = mockUsers
  }
}

onMounted(async () => {
  loadData()
  try {
    heartbeats.value = await getHeartbeats() as unknown as LikeUser[]
  } catch {
    heartbeats.value = mockUsers.slice(0, 3)
  }
})

watch(activeTab, () => loadData())
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.likes-page {
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

.heartbeats-section {
  padding: 16px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;

  h3 {
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: 18px;
    font-weight: 700;
    margin: 0;
  }

  .badge {
    background: $primary;
    color: #fff;
    font-size: 11px;
    padding: 2px 8px;
    border-radius: $radius-full;
    font-weight: 600;
  }
}

.heartbeat-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  -webkit-overflow-scrolling: touch;

  &::-webkit-scrollbar { display: none; }
}

.heartbeat-card {
  min-width: 90px;
  text-align: center;

  .heartbeat-avatar {
    width: 72px;
    height: 90px;
    border-radius: $radius-md;
    object-fit: cover;
  }

  .heartbeat-name {
    display: block;
    font-size: 13px;
    font-weight: 600;
    margin-top: 6px;
    color: $on-surface;
  }

  .heartbeat-score {
    font-size: 11px;
    color: $primary;
    font-weight: 600;
  }
}

.user-list {
  padding: 0 16px;
}

.like-user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid $surface-container-low;

  .user-avatar {
    width: 56px;
    height: 56px;
    border-radius: $radius-md;
    object-fit: cover;
  }

  .user-info {
    flex: 1;

    h4 {
      margin: 0;
      font-size: 16px;
      font-weight: 600;
      .age { color: $on-surface-variant; font-weight: 400; }
    }

    .profession, .location {
      margin: 2px 0 0;
      font-size: 13px;
      color: $on-surface-variant;
    }
  }

  .match-score {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;

    span {
      font-size: 14px;
      font-weight: 700;
      color: $primary;
    }
  }
}
</style>
