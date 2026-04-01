<template>
  <div class="profile-page">
    <EtherealNavBar>
      <template #title><span class="brand-title">Ethereal</span></template>
    </EtherealNavBar>

    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <img :src="userInfo?.avatar || 'https://picsum.photos/80/80'" class="profile-avatar" />
          <van-icon v-if="userInfo?.verified" name="certificate" color="#FF4D6D" size="18" class="verified-badge" />
        </div>
        <div class="profile-info">
          <h2>{{ userInfo?.nickname || 'Elena Zhao' }}</h2>
          <p class="location">
            <van-icon name="location-o" size="14" />
            {{ userInfo?.city || 'Shanghai, China' }}
          </p>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-value">1.2k</span>
          <span class="stat-label">Spark Matches</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-value">84%</span>
          <span class="stat-label">Vibe Score</span>
        </div>
      </div>

      <div class="verified-row" v-if="userInfo?.verified">
        <van-icon name="certificate" color="#FF4D6D" size="16" />
        <span>Verified</span>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <h3 class="menu-title">账户与资料</h3>
      <div class="menu-card">
        <div class="menu-item" @click="$router.push('/profile/edit')">
          <van-icon name="edit" size="20" />
          <span>编辑个人资料</span>
          <van-icon name="arrow" size="16" color="#abadae" />
        </div>
        <div class="menu-item">
          <van-icon name="shield-o" size="20" />
          <span>身份验证</span>
          <span class="menu-tag">推荐</span>
          <van-icon name="arrow" size="16" color="#abadae" />
        </div>
      </div>
    </div>

    <div class="menu-section">
      <h3 class="menu-title">系统与社区</h3>
      <div class="menu-card">
        <div class="menu-item">
          <van-icon name="setting-o" size="20" />
          <span>设置</span>
          <van-icon name="arrow" size="16" color="#abadae" />
        </div>
        <div class="menu-item">
          <van-icon name="comment-o" size="20" />
          <span>反馈</span>
          <van-icon name="arrow" size="16" color="#abadae" />
        </div>
        <div class="menu-item">
          <van-icon name="friends-o" size="20" />
          <span>邀请朋友</span>
          <span class="menu-tag gold">获取权益</span>
          <van-icon name="arrow" size="16" color="#abadae" />
        </div>
      </div>
    </div>

    <div class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import EtherealNavBar from '@/components/common/EtherealNavBar.vue'
import { useAuthStore } from '@/stores/auth'
import { useUserStore } from '@/stores/user'

const authStore = useAuthStore()
const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)

onMounted(async () => {
  try {
    await userStore.fetchProfile()
    userInfo.value = userStore.userInfo
  } catch {}
})

function handleLogout() {
  authStore.logout()
}
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables.scss' as *;

.profile-page {
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

.profile-card {
  margin: 16px;
  background: $surface-container-lowest;
  border-radius: $radius-lg;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-section { position: relative; }

.profile-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  object-fit: cover;
}

.verified-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #fff;
  border-radius: 50%;
}

.profile-info {
  h2 {
    margin: 0;
    font-family: 'Plus Jakarta Sans', sans-serif;
    font-size: 22px;
    font-weight: 700;
  }
  .location {
    margin: 4px 0 0;
    color: $on-surface-variant;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.stats-row {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid $surface-container-low;
}

.stat-item {
  flex: 1;
  text-align: center;

  .stat-value {
    display: block;
    font-size: 22px;
    font-weight: 700;
    font-family: 'Plus Jakarta Sans', sans-serif;
    color: $primary;
  }
  .stat-label {
    font-size: 12px;
    color: $on-surface-variant;
    margin-top: 2px;
  }
}

.stat-divider {
  width: 1px;
  height: 32px;
  background: $surface-container-high;
}

.verified-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 14px;
  font-size: 14px;
  color: $primary;
  font-weight: 600;
}

.menu-section { padding: 0 16px; margin-top: 20px; }

.menu-title {
  font-size: 14px;
  font-weight: 600;
  color: $on-surface-variant;
  margin: 0 0 8px 4px;
}

.menu-card {
  background: $surface-container-lowest;
  border-radius: $radius-lg;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  cursor: pointer;

  &:not(:last-child) { border-bottom: 1px solid $surface-container-low; }

  span:first-of-type { flex: 1; font-size: 15px; color: $on-surface; }

  .van-icon:first-child { color: $on-surface-variant; }
}

.menu-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: $radius-full;
  background: rgba($primary, 0.1);
  color: $primary;
  font-weight: 600;

  &.gold {
    background: rgba($secondary, 0.1);
    color: $secondary;
  }
}

.logout-section {
  padding: 24px 16px;
}

.logout-btn {
  width: 100%;
  height: 48px;
  border: 1px solid $outline-variant;
  border-radius: $radius-lg;
  background: transparent;
  color: $on-surface-variant;
  font-size: 15px;
  cursor: pointer;
}
</style>
