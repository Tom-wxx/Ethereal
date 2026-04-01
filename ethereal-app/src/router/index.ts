import { createRouter, createWebHistory } from 'vue-router'
import { setupGuards } from './guards'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/auth/LoginPage.vue'),
      meta: { hideTabBar: true }
    },
    {
      path: '/',
      redirect: '/discovery'
    },
    {
      path: '/discovery',
      name: 'Discovery',
      component: () => import('@/views/discovery/DiscoveryPage.vue')
    },
    {
      path: '/moments',
      name: 'Moments',
      component: () => import('@/views/moments/MomentsPage.vue')
    },
    {
      path: '/likes',
      name: 'Likes',
      component: () => import('@/views/likes/LikesPage.vue')
    },
    {
      path: '/messages',
      name: 'Messages',
      component: () => import('@/views/messages/MessagesPage.vue')
    },
    {
      path: '/profile',
      name: 'Profile',
      component: () => import('@/views/profile/ProfilePage.vue')
    },
    {
      path: '/chat/:id',
      name: 'ChatRoom',
      component: () => import('@/views/messages/ChatRoom.vue'),
      meta: { hideTabBar: true }
    },
    {
      path: '/profile/edit',
      name: 'EditProfile',
      component: () => import('@/views/profile/EditProfile.vue'),
      meta: { hideTabBar: true }
    }
  ]
})

setupGuards(router)

export default router
