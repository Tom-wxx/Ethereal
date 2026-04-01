import request from './request'

export interface SystemConfigItem {
  key: string
  value: string
  label: string
  type: 'text' | 'number' | 'switch' | 'textarea'
}

/** 获取系统配置列表 */
export function getConfigList() {
  return request.get('/config')
}

/** 更新系统配置 */
export function updateConfig(key: string, value: string) {
  return request.put(`/config/${key}`, { value })
}

/** 批量更新系统配置 */
export function batchUpdateConfig(configs: { key: string; value: string }[]) {
  return request.put('/config/batch', { configs })
}
