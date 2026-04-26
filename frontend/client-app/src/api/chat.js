import api from './axios';

export const sendMessage = (data) => api.post('/api/chat/message', data);
export const getChatHistory = (conversationId) =>
  api.get(`/api/chat/history/${conversationId}`);