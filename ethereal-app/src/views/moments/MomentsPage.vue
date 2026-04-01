<template>
  <div class="moments-page">
    <EtherealNavBar>
      <template #title><span class="brand-title">Ethereal</span></template>
      <template #right><van-icon name="setting-o" size="22" color="#595c5d" /></template>
    </EtherealNavBar>

    <van-tabs v-model:active="activeTab" color="#FF4D6D" line-width="24" shrink>
      <van-tab title="附近动态" />
      <van-tab title="热门" />
      <van-tab title="最新" />
    </van-tabs>

    <div class="moments-list">
      <div v-for="moment in moments" :key="moment.id" class="moment-card">
        <div class="moment-header">
          <img :src="moment.avatar || 'https://picsum.photos/40/40?random=' + moment.id" class="moment-avatar" />
          <div class="moment-user">
            <span class="nickname">{{ moment.nickname }}</span>
            <span class="distance">{{ moment.distance ? formatDistance(moment.distance) + ' away' : '' }}</span>
          </div>
          <van-icon name="ellipsis" size="20" color="#595c5d" />
        </div>

        <p class="moment-content">{{ moment.content }}</p>

        <div v-if="moment.images?.length" class="moment-images" :class="'grid-' + Math.min(moment.images.length, 3)">
          <img v-for="(img, idx) in moment.images.slice(0, 9)" :key="idx" :src="img || 'https://picsum.photos/200/200?random=' + moment.id + idx" class="moment-img" />
        </div>

        <div class="moment-actions">
          <div class="action-item" @click="handleLike(moment)">
            <van-icon :name="moment.isLiked ? 'like' : 'like-o'" :color="moment.isLiked ? '#FF4D6D' : '#595c5d'" size="18" />
            <span>{{ moment.likeCount }}</span>
          </div>
          <div class="action-item">
            <van-icon name="chat-o" size="18" color="#595c5d" />
            <span>{{ moment.commentCount }}</span>
          </div>
          <button class="say-hi-btn" @click="handleSayHi(moment)">Say Hi</button>
        </div>
      </div>
    </div>

    <!-- 发布按钮 -->
    <div class="fab-btn" @click="showPublish = true">
      <van-icon name="plus" size="24" color="#fff" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { formatDistance } from '@/utils/format'
import type { Moment } from '@/types/moment'

const router = useRouter()
const activeTab = ref(0)
const showPublish = ref(false)

const moments = ref<Moment[]>([
  { id: 1, userId: 3, nickname: 'Chen Wei', avatar: '', content: 'Found this hidden rooftop cafe in Jing\'an. The light here is absolutely ethereal during the blue hour...', images: ['', ''], locationName: '静安区', distance: 500, likeCount: 124, commentCount: 18, isLiked: false, createdAt: '' },
  { id: 2, userId: 5, nickname: 'Lina', avatar: '', content: 'Early morning run by the river. The mist was something else today...', images: [''], locationName: '黄浦江畔', distance: 1200, likeCount: 89, commentCount: 12, isLiked: true, createdAt: '' },
  { id: 3, userId: 7, nickname: 'Marcus', avatar: '', content: 'New exhibition at the West Bund Museum. Absolutely worth visiting this weekend.', images: ['', '', ''], locationName: '西岸美术馆', distance: 3500, likeCount: 67, commentCount: 8, isLiked: false, createdAt: '' },
])

function handleLike(moment: Moment) {
  moment.isLiked = !moment.isLiked
  moment.likeCount += moment.isLiked ? 1 : -1
}

function handleSayHi(moment: Moment) {
  router.push(`/chat/${moment.userId}`)
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.moments-page {
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

.moments-list { padding: 12px 16px; }

.moment-card {
  background: $surface-container-lowest;
  border-radius: $radius-lg;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.moment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;

  .moment-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    object-fit: cover;
  }

  .moment-user {
    flex: 1;
    .nickname { display: block; font-weight: 600; font-size: 15px; color: $on-surface; }
    .distance { font-size: 12px; color: $on-surface-variant; }
  }
}

.moment-content {
  font-size: 15px;
  line-height: 1.6;
  color: $on-surface;
  margin: 0 0 12px;
}

.moment-images {
  display: grid;
  gap: 4px;
  border-radius: $radius-md;
  overflow: hidden;
  margin-bottom: 12px;

  &.grid-1 { grid-template-columns: 1fr; }
  &.grid-2 { grid-template-columns: 1fr 1fr; }
  &.grid-3 { grid-template-columns: 1fr 1fr 1fr; }

  .moment-img {
    width: 100%;
    aspect-ratio: 1;
    object-fit: cover;
  }
}

.moment-actions {
  display: flex;
  align-items: center;
  gap: 20px;

  .action-item {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
    span { font-size: 13px; color: $on-surface-variant; }
  }

  .say-hi-btn {
    margin-left: auto;
    background: linear-gradient(135deg, $primary, $primary-container);
    color: #fff;
    border: none;
    border-radius: $radius-full;
    padding: 6px 16px;
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
  }
}

.fab-btn {
  position: fixed;
  bottom: 80px;
  right: 20px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, $primary, $primary-container);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba($primary, 0.3);
  cursor: pointer;
  z-index: 10;
}
</style>
