import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAdminStore } from '@/stores/admin'
import router from '@/router'

const request = axios.create({
  baseURL: '/admin-api',
  timeout: 15000
})

// 请求拦截器 — 注入 Token
request.interceptors.request.use(
  (config) => {
    const adminStore = useAdminStore()
    if (adminStore.token) {
      config.headers.Authorization = `Bearer ${adminStore.token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== undefined && res.code !== 0 && res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    if (error.response?.status === 401) {
      const adminStore = useAdminStore()
      adminStore.logout()
      router.push('/login')
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.response?.data?.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
