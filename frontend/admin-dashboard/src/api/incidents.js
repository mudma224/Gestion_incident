import api from './axios';

export const getAllIncidents = () => api.get('/api/incidents');
export const getIncidentById = (id) => api.get(`/api/incidents/${id}`);
export const updateStatus = (id, status) =>
  api.patch(`/api/incidents/${id}/status?status=${status}`);
export const assignIncident = (id, technicienId) =>
  api.patch(`/api/incidents/${id}/assigner?technicienId=${technicienId}`);
export const deleteIncident = (id) => api.delete(`/api/incidents/${id}`);