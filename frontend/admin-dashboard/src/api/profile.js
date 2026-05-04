import api from './axios';

export const syncCurrentUser = () => api.post('/api/users/sync');
