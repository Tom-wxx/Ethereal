export interface UserInfo {
  id: number
  phone: string
  nickname: string
  avatar: string
  gender: number
  birthday: string
  bio: string
  profession: string
  city: string
  longitude: number
  latitude: number
  verified: number
  vibeScore: number
  vipLevel: number
  tags: UserTag[]
  photos: UserPhoto[]
}

export interface UserTag {
  id: number
  tagName: string
  tagCategory: string
}

export interface UserPhoto {
  id: number
  photoUrl: string
  sortOrder: number
}

export interface DiscoveryCard {
  id: number
  nickname: string
  avatar: string
  age: number
  gender: number
  profession: string
  city: string
  distance: number
  verified: number
  vibeScore: number
  tags: UserTag[]
  photos: UserPhoto[]
  bio: string
}

export interface LikeUser {
  id: number
  nickname: string
  avatar: string
  age: number
  profession: string
  city: string
  matchScore: number
  createdAt: string
}
