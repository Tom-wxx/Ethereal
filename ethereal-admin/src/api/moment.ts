import request from './request'

export interface MomentQuery {
  status?: string
  page: number
  pageSize: number
}

export interface MomentItem {
  id: number
  userId: number
  nickname: string
  content: string
  images: string[]
  status: string
  createdAt: string
}

/** 动态列表 */
export function getMomentList(params: MomentQuery) {
  return request.get('/content/moments', { params })
}

/** 审核通过 */
export function approveMoment(id: number) {
  return request.post(`/content/moments/${id}/approve`)
}

/** 审核拒绝 */
export function rejectMoment(id: number, reason: string) {
  return request.post(`/content/moments/${id}/reject`, { reason })
}

/** 图片审核列表 */
export function getImageAuditList(params: MomentQuery) {
  return request.get('/content/photos', { params })
}

/** 图片审核通过 */
export function approveImage(id: number) {
  return request.post(`/content/photos/${id}/approve`)
}

/** 图片审核拒绝 */
export function rejectImage(id: number, reason: string) {
  return request.post(`/content/photos/${id}/reject`, { reason })
}
