import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAdminStore } from '@/stores/admin'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/AdminLogin.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/AdminLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/user/UserListView.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'users/:id',
        name: 'UserDetail',
        component: () => import('@/views/user/UserDetailView.vue'),
        meta: { title: '用户详情' }
      },
      {
        path: 'content/moments',
        name: 'MomentAudit',
        component: () => import('@/views/content/MomentAuditView.vue'),
        meta: { title: '动态审核' }
      },
      {
        path: 'content/photos',
        name: 'ImageAudit',
        component: () => import('@/views/content/ImageAuditView.vue'),
        meta: { title: '图片审核' }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('@/views/stats/StatsView.vue'),
        meta: { title: '数据统计' }
      },
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/config/SystemConfig.vue'),
        meta: { title: '系统配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const adminStore = useAdminStore()
  const { token } = storeToRefs(adminStore)
  const isLoginPage = to.path === '/login' || to.name === 'Login'

  if (isLoginPage) {
    if (token.value) {
      next({ path: '/dashboard', replace: true })
    } else {
      next()
    }
    return
  }

  if (!token.value) {
    next({ path: '/login', replace: true })
    return
  }
  next()
})

export default router
