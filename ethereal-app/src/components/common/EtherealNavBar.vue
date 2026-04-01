<template>
  <div class="ethereal-navbar">
    <div v-if="showBack" class="navbar-left" @click="goBack">
      <van-icon name="arrow-left" size="22" />
    </div>
    <div class="navbar-title">
      <slot name="title">{{ title }}</slot>
    </div>
    <div class="navbar-right">
      <slot name="right" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

defineProps<{
  title?: string
  showBack?: boolean
}>()

const router = useRouter()
function goBack() {
  router.back()
}
</script>

<style lang="scss" scoped>
.ethereal-navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 48px;
  padding: 0 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-title {
  font-family: 'Plus Jakarta Sans', 'PingFang SC', sans-serif;
  font-size: 18px;
  font-weight: 700;
  color: #2c2f30;
}

.navbar-left, .navbar-right {
  min-width: 40px;
  display: flex;
  align-items: center;
}

.navbar-right {
  justify-content: flex-end;
}
</style>
