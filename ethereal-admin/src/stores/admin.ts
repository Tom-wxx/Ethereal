import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminLogin as loginApi, type LoginParams } from '@/api/auth'

interface AdminInfo {
  id: number
  username: string
  nickname: string
  role: string
}

export const useAdminStore = defineStore('admin', () => {
  const token = ref<string>(localStorage.getItem('admin_token') || '')
  const adminInfo = ref<AdminInfo | null>(null)

  async function login(params: LoginParams) {
    const res = (await loginApi(params)) as {
      code?: number
      data?: {
        token: string
        adminId: number
        username: string
        role: string
      }
    }
    const payload = res.data
    if (!payload?.token) {
      throw new Error('登录响应异常')
    }
    token.value = payload.token
    adminInfo.value = {
      id: payload.adminId,
      username: payload.username,
      nickname: payload.username,
      role: payload.role,
    }
    localStorage.setItem('admin_token', payload.token)
    return payload
  }

  function logout() {
    token.value = ''
    adminInfo.value = null
    localStorage.removeItem('admin_token')
  }

  function setAdminInfo(info: AdminInfo) {
    adminInfo.value = info
  }

  return { token, adminInfo, login, logout, setAdminInfo }
})
