import axios from 'axios';

// On pointe directement vers l'incident-service (8081) au lieu de la gateway
const api = axios.create({
    baseURL: 'http://localhost:8081', // Port 8081 et on retire /api
});
export const getIncidents = () => api.get('/incidents');
export const createIncident = (incident) => api.post('/incidents', incident);

export default api;