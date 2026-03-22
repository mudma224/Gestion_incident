import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8081", // backend
});

export const createIncident = (data) =>
  api.post("/incidents", data);

export const getIncidents = () =>
  api.get("/incidents");