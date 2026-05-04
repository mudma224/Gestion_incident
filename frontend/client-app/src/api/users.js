import api from './axios';

export const syncCurrentUser = () => api.post('/api/users/sync');
export const getCurrentUser = () => api.get('/api/users/me');
