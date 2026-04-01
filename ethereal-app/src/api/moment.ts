import request from './request'

export function getNearby(params: { longitude: number; latitude: number; page?: number; size?: number }) {
  return request.get('/moments/nearby', { params })
}

export function getTrending(page = 1, size = 10) {
  return request.get('/moments/trending', { params: { page, size } })
}

export function getLatest(page = 1, size = 10) {
  return request.get('/moments/latest', { params: { page, size } })
}

export function createMoment(data: { content: string; images: string[]; longitude?: number; latitude?: number; locationName?: string }) {
  return request.post('/moments', data)
}

export function likeMoment(id: number) {
  return request.post(`/moments/${id}/like`)
}

export function unlikeMoment(id: number) {
  return request.delete(`/moments/${id}/like`)
}

export function getComments(id: number, page = 1, size = 20) {
  return request.get(`/moments/${id}/comments`, { params: { page, size } })
}

export function addComment(id: number, content: string, parentId?: number) {
  return request.post(`/moments/${id}/comments`, { content, parentId })
}
