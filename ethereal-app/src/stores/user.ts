import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProfile, updateProfile as updateProfileApi } from '@/api/user'
import type { UserInfo } from '@/types/user'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)

  async function fetchProfile() {
    userInfo.value = await getProfile() as unknown as UserInfo
  }

  async function updateProfile(data: Partial<UserInfo>) {
    await updateProfileApi(data as Record<string, unknown>)
    await fetchProfile()
  }

  return { userInfo, fetchProfile, updateProfile }
})
