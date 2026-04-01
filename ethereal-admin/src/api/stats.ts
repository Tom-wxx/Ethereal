import request from './request'

/** 总览统计 */
export function getOverviewStats() {
  return request.get('/stats/overview')
}

/** 用户增长趋势（最近14天） */
export function getUserGrowthTrend() {
  return request.get('/stats/growth')
}

/** 最近注册用户 */
export function getRecentUsers(limit: number = 5) {
  return request.get('/stats/recent-users', { params: { limit } })
}
