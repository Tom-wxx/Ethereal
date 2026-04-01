import type { Router } from 'vue-router'
import { storage } from '@/utils/storage'

const whiteList = ['/login']

export function setupGuards(router: Router) {
  router.beforeEach((to, _from, next) => {
    const token = storage.get('token')
    if (token) {
      if (to.path === '/login') {
        next('/')
      } else {
        next()
      }
    } else {
      if (whiteList.includes(to.path)) {
        next()
      } else {
        next('/login')
      }
    }
  })
}
