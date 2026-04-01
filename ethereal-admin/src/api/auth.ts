import request from './request'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  adminInfo: {
    id: number
    username: string
    nickname: string
    role: string
  }
}

/** 管理员登录 */
export function adminLogin(data: LoginParams) {
  return request.post<any, LoginResult>('/auth/login', data)
}

/** 获取当前管理员信息 */
export function getAdminInfo() {
  return request.get<any, LoginResult['adminInfo']>('/auth/info')
}

/** 退出登录 */
export function adminLogout() {
  return request.post('/auth/logout')
}
