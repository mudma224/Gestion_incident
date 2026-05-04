import api from './axios';

export const getAllUsers = () => api.get('/api/users');
export const getUserById = (id) => api.get(`/api/users/${id}`);