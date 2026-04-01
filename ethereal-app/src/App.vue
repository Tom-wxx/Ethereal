<template>
  <div id="ethereal-app">
    <router-view v-slot="{ Component, route }">
      <keep-alive :include="keepAliveList">
        <component :is="Component" :key="route.path" />
      </keep-alive>
    </router-view>
    <EtherealTabBar v-if="showTabBar" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import EtherealTabBar from '@/components/common/EtherealTabBar.vue'

const route = useRoute()

const keepAliveList = ['DiscoveryPage', 'MomentsPage', 'LikesPage', 'MessagesPage', 'ProfilePage']

const tabBarRoutes = ['/discovery', '/moments', '/likes', '/messages', '/profile']

const showTabBar = computed(() => {
  return tabBarRoutes.includes(route.path)
})
</script>

<style lang="scss">
#ethereal-app {
  width: 100%;
  min-height: 100vh;
}
</style>
