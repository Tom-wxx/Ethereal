<template>
  <van-tabbar v-model="active" :active-color="primaryColor" inactive-color="#757778" :border="false" :safe-area-inset-bottom="true" class="ethereal-tabbar">
    <van-tabbar-item to="/discovery" icon="search">探索</van-tabbar-item>
    <van-tabbar-item to="/moments" icon="fire-o">发现</van-tabbar-item>
    <van-tabbar-item to="/likes" icon="like-o">喜欢</van-tabbar-item>
    <van-tabbar-item to="/messages" icon="chat-o">聊天</van-tabbar-item>
    <van-tabbar-item to="/profile" icon="contact">我的</van-tabbar-item>
  </van-tabbar>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const primaryColor = '#FF4D6D'
const route = useRoute()
const active = ref(0)

const tabMap: Record<string, number> = {
  '/discovery': 0,
  '/moments': 1,
  '/likes': 2,
  '/messages': 3,
  '/profile': 4
}

watch(() => route.path, (path) => {
  if (path in tabMap) {
    active.value = tabMap[path]
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.ethereal-tabbar {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}
</style>
