const PREFIX = 'ethereal_'

export const storage = {
  get(key: string): string | null {
    return localStorage.getItem(PREFIX + key)
  },
  set(key: string, value: string): void {
    localStorage.setItem(PREFIX + key, value)
  },
  remove(key: string): void {
    localStorage.removeItem(PREFIX + key)
  },
  getJSON<T>(key: string): T | null {
    const val = localStorage.getItem(PREFIX + key)
    if (!val) return null
    try {
      return JSON.parse(val) as T
    } catch {
      return null
    }
  },
  setJSON(key: string, value: unknown): void {
    localStorage.setItem(PREFIX + key, JSON.stringify(value))
  }
}
