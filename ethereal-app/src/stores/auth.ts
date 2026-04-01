import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storage } from '@/utils/storage'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(storage.get('token') || '')

  const isLoggedIn = computed(() => !!token.value)

  async function login(phone: string, code: string) {
    const data: any = await loginApi(phone, code)
    token.value = data.token
    storage.set('token', data.token)
    router.push('/')
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      storage.remove('token')
      router.push('/login')
    }
  }

  function checkAuth() {
    return !!storage.get('token')
  }

  return { token, isLoggedIn, login, logout, checkAuth }
})
