import api from './axios';

export const getMyNotifications  = ()    => api.get('/api/notifications/me');
export const countUnread         = ()    => api.get('/api/notifications/me/unread/count');
export const markAsRead          = (id)  => api.patch(`/api/notifications/${id}/read`);
export const deleteAllMyNotifs   = ()    => api.delete('/api/notifications/me');