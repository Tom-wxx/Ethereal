import request from './request'

export function sendSmsCode(phone: string) {
  return request.post('/auth/sms-code', { phone })
}

export function login(phone: string, code: string) {
  return request.post('/auth/login', { phone, code })
}

export function wxLogin(wxCode: string) {
  return request.post('/auth/wx-login', { wxCode })
}

export function logout() {
  return request.post('/auth/logout')
}
