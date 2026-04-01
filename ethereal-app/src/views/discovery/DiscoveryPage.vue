<template>
  <div class="discovery-page">
    <EtherealNavBar>
      <template #title>
        <span class="brand-title">Ethereal</span>
      </template>
      <template #right>
        <van-icon name="setting-o" size="22" color="#595c5d" />
      </template>
    </EtherealNavBar>

    <div class="card-container">
      <div v-if="cards.length" class="user-card">
        <div class="card-image">
          <img :src="currentCard.avatar || 'https://picsum.photos/400/500'" :alt="currentCard.nickname" />
          <div class="card-gradient"></div>
        </div>
        <div class="card-info">
          <div class="card-header">
            <h2>{{ currentCard.nickname }} <span class="age">{{ currentCard.age }}</span></h2>
            <van-icon v-if="currentCard.verified" name="certificate" color="#FF4D6D" size="20" />
          </div>
          <div class="card-location">
            <van-icon name="location-o" size="14" />
            <span>{{ currentCard.city }}, {{ formatDistance(currentCard.distance || 0) }}</span>
          </div>
          <div class="card-tags">
            <span v-for="tag in currentCard.tags?.slice(0, 4)" :key="tag.tagName" class="tag">
              {{ tag.tagName }}
            </span>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <van-icon name="search" size="48" color="#abadae" />
        <p>正在寻找附近的人...</p>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="action-btn secondary" @click="handleAction('pass')">
        <van-icon name="cross" size="24" />
      </div>
      <div class="action-btn surprise" @click="handleSurprise">
        <van-icon name="star-o" size="22" />
      </div>
      <div class="action-btn primary" @click="handleAction('like')">
        <van-icon name="like-o" size="26" color="#fff" />
      </div>
      <div class="action-btn secondary" @click="handleAction('super_like')">
        <van-icon name="chat-o" size="22" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { showToast } from 'vant'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { getCards, doAction } from '@/api/discovery'
import { formatDistance } from '@/utils/format'
import type { DiscoveryCard } from '@/types/user'

const cards = ref<DiscoveryCard[]>([])
const cardIndex = ref(0)

const currentCard = computed(() => cards.value[cardIndex.value] || {} as DiscoveryCard)

onMounted(async () => {
  try {
    const data = await getCards() as unknown as DiscoveryCard[]
    cards.value = data || []
  } catch {
    // 使用mock数据
    cards.value = [
      { id: 1, nickname: 'Mei', age: 24, avatar: '', gender: 2, profession: 'UX Designer', city: 'Shanghai', distance: 2000, verified: 1, vibeScore: 84, bio: 'Love jazz and minimalism', tags: [{ id: 1, tagName: 'Jazz', tagCategory: 'hobby' }, { id: 2, tagName: 'Minimalism', tagCategory: 'lifestyle' }, { id: 3, tagName: 'Architecture', tagCategory: 'hobby' }], photos: [] }
    ]
  }
})

async function handleAction(action: 'like' | 'pass' | 'super_like') {
  if (!currentCard.value.id) return
  try {
    await doAction(currentCard.value.id, action)
  } catch {
    // 离线模式
  }
  if (cardIndex.value < cards.value.length - 1) {
    cardIndex.value++
  } else {
    cards.value = []
    showToast('暂时没有更多推荐了')
  }
}

function handleSurprise() {
  showToast('AI 惊喜推荐')
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.discovery-page {
  min-height: 100vh;
  background: $surface;
  display: flex;
  flex-direction: column;
  padding-bottom: 80px;
}

.brand-title {
  font-family: 'Plus Jakarta Sans', sans-serif;
  font-weight: 800;
  background: linear-gradient(135deg, $primary, $secondary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-container {
  flex: 1;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-card {
  width: 100%;
  max-width: 360px;
  border-radius: $radius-lg;
  overflow: hidden;
  background: $surface-container-lowest;
  box-shadow: 0 12px 40px rgba(44, 47, 48, 0.08);
}

.card-image {
  position: relative;
  width: 100%;
  height: 380px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .card-gradient {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 120px;
    background: linear-gradient(transparent, rgba(0,0,0,0.5));
  }
}

.card-info {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;

  h2 {
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: 24px;
    font-weight: 700;
    margin: 0;
    color: $on-surface;
  }

  .age {
    font-weight: 400;
    color: $on-surface-variant;
  }
}

.card-location {
  display: flex;
  align-items: center;
  gap: 4px;
  color: $on-surface-variant;
  font-size: 14px;
  margin-top: 6px;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;

  .tag {
    padding: 6px 14px;
    background: $surface-container-high;
    border-radius: $radius-full;
    font-size: 13px;
    color: $on-surface;
  }
}

.empty-state {
  text-align: center;
  color: $outline;

  p { margin-top: 12px; }
}

.action-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  position: fixed;
  bottom: 60px;
  left: 0;
  right: 0;
}

.action-btn {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;

  &:active { transform: scale(0.9); }

  &.primary {
    width: 64px;
    height: 64px;
    background: linear-gradient(135deg, $primary, $primary-container);
    color: #fff;
    box-shadow: 0 8px 24px rgba($primary, 0.3);
  }

  &.secondary {
    background: $surface-container-lowest;
    color: $on-surface-variant;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  }

  &.surprise {
    background: linear-gradient(135deg, $secondary, $secondary-container);
    color: #fff;
    box-shadow: 0 8px 24px rgba($secondary, 0.3);
  }
}
</style>
