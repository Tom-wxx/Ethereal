import request from './request'

export function getConversations() {
  return request.get('/chat/conversations')
}

export function getMessages(conversationId: number, params?: { before?: string; size?: number }) {
  return request.get(`/chat/messages/${conversationId}`, { params })
}

export function markRead(conversationId: number) {
  return request.post(`/chat/conversations/${conversationId}/read`)
}
