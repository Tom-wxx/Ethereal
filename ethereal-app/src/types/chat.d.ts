export interface Conversation {
  id: number
  targetUser: {
    id: number
    nickname: string
    avatar: string
  }
  lastMsgContent: string
  lastMsgTime: string
  unreadCount: number
}

export interface ChatMessage {
  id: number
  conversationId: number
  senderId: number
  msgType: number
  content: string
  isRead: number
  createdAt: string
}
