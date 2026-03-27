import axios from 'axios';

// Instance pointant vers la Gateway (port 8080)
const api = axios.create({
    baseURL: 'http://localhost:8080/api', 
});

// Service Incident
export const getIncidents = () => api.get('/incidents');
export const createIncident = (incident) => api.post('/incidents', incident);

// Service Comment
export const getCommentsByIncident = (incidentId) => api.get(`/comments/incident/${incidentId}`);
export const createComment = (comment) => api.post('/comments', comment);

export default api;