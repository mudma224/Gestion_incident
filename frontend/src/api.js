import axios from 'axios';

// On crée une instance Axios configurée pour pointer vers ta Gateway
const api = axios.create({
    baseURL: 'http://localhost:8080/api', 
});

// Fonctions pour appeler tes services via la Gateway
export const getIncidents = () => api.get('/incidents');
export const createIncident = (incident) => api.post('/incidents', incident);

export default api;