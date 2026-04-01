import request from './request'

export function getWhoLikesMe(page = 1, size = 10) {
  return request.get('/likes/who-likes-me', { params: { page, size } })
}

export function getILike(page = 1, size = 10) {
  return request.get('/likes/i-like', { params: { page, size } })
}

export function getMutual(page = 1, size = 10) {
  return request.get('/likes/mutual', { params: { page, size } })
}

export function getHeartbeats() {
  return request.get('/likes/heartbeats')
}
