import api from './axios';

export const getMyIncidents = () => api.get('/api/incidents/mes-incidents');
export const createIncident = (data) => api.post('/api/incidents', data);
export const getIncidentById = (id) => api.get(`/api/incidents/${id}`);