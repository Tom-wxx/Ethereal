import request from './request'

export function getCards(params?: { gender?: number; ageMin?: number; ageMax?: number; distance?: number }) {
  return request.get('/discovery/cards', { params })
}

export function doAction(targetUserId: number, action: 'like' | 'pass' | 'super_like') {
  return request.post('/discovery/action', { targetUserId, action })
}

export function getSurprise() {
  return request.get('/discovery/surprise')
}
