import request from './request'

export interface UserQuery {
  keyword?: string
  status?: string
  page: number
  pageSize: number
}

export interface UserItem {
  id: number
  nickname: string
  phone: string
  avatar: string
  gender: string
  city: string
  verified: boolean
  status: string
  createdAt: string
}

export interface UserDetail extends UserItem {
  bio: string
  birthday: string
  photos: string[]
  matchCount: number
  momentCount: number
}

/** 用户列表 */
export function getUserList(params: UserQuery) {
  return request.get('/users', { params })
}

/** 用户详情 */
export function getUserDetail(id: number) {
  return request.get(`/users/${id}`)
}

/** 封禁用户 */
export function banUser(id: number, reason: string) {
  return request.post(`/users/${id}/ban`, { reason })
}

/** 解封用户 */
export function unbanUser(id: number) {
  return request.post(`/users/${id}/unban`)
}

/** 认证审核 */
export function reviewVerification(id: number, approved: boolean, reason?: string) {
  return request.post(`/users/${id}/verify`, { approved, reason })
}
