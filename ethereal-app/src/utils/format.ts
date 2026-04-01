export function formatDistance(meters: number): string {
  if (meters < 1000) return `${Math.round(meters)}m`
  return `${(meters / 1000).toFixed(1)}km`
}

export function formatTime(datetime: string): string {
  const now = Date.now()
  const target = new Date(datetime).getTime()
  const diff = now - target
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours}小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days}天前`
  return new Date(datetime).toLocaleDateString('zh-CN')
}

export function formatCount(num: number): string {
  if (num < 1000) return String(num)
  if (num < 10000) return `${(num / 1000).toFixed(1)}k`
  return `${(num / 10000).toFixed(1)}w`
}
