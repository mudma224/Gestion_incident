import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8081",
});

export const getIncidents = () => api.get("/incidents");

export const createIncident = (incident) =>
  api.post("/incidents", incident);

export default api;