import request from './request'

export function getProfile() {
  return request.get('/user/profile')
}

export function updateProfile(data: Record<string, unknown>) {
  return request.put('/user/profile', data)
}

export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/user/avatar', formData)
}

export function updateLocation(longitude: number, latitude: number) {
  return request.put('/user/location', { longitude, latitude })
}

export function updateTags(tags: string[]) {
  return request.post('/user/tags', { tags })
}

export function getUserDetail(id: number) {
  return request.get(`/user/${id}`)
}
