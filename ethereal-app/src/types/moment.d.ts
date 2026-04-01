export interface Moment {
  id: number
  userId: number
  nickname: string
  avatar: string
  content: string
  images: string[]
  locationName: string
  distance: number
  likeCount: number
  commentCount: number
  isLiked: boolean
  createdAt: string
}

export interface MomentComment {
  id: number
  momentId: number
  userId: number
  nickname: string
  avatar: string
  content: string
  parentId: number
  createdAt: string
}
