import api from './axios';

export const getCommentsByIncident = (incidentId) =>
  api.get(`/api/comments/incident/${incidentId}`);
export const createComment = (data) => api.post('/api/comments', data);