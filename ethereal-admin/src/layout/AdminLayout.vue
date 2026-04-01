<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <h1>Ethereal</h1>
        <span>管理后台</span>
      </div>
      <el-menu :default-active="activeMenu" router class="sidebar-menu" background-color="#1a1a2e" text-color="#a0a0b0" active-text-color="#FF4D6D">
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-sub-menu index="content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容审核</span>
          </template>
          <el-menu-item index="/content/moments">动态审核</el-menu-item>
          <el-menu-item index="/content/photos">图片审核</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/stats">
          <el-icon><TrendCharts /></el-icon>
          <span>数据统计</span>
        </el-menu-item>
        <el-menu-item index="/config">
          <el-icon><Setting /></el-icon>
          <span>系统配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="topbar">
        <div class="topbar-left">
          <span class="page-title">{{ currentTitle }}</span>
        </div>
        <div class="topbar-right">
          <span class="admin-name">{{ adminStore.adminInfo?.username || 'Admin' }}</span>
          <el-button text @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import { DataBoard, User, Document, TrendCharts, Setting } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => (route.meta.title as string) || '数据概览')

function handleLogout() {
  adminStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: #1a1a2e;
  overflow-y: auto;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);

  h1 {
    margin: 0;
    font-size: 24px;
    font-weight: 800;
    background: linear-gradient(135deg, #FF4D6D, #FF9F1C);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    font-family: 'Plus Jakarta Sans', sans-serif;
  }

  span {
    font-size: 12px;
    color: #666;
  }
}

.sidebar-menu {
  border-right: none;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  height: 56px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;

  .admin-name {
    font-size: 14px;
    color: #666;
  }
}

.main-content {
  background: #f5f6f7;
  overflow-y: auto;
}
</style>
