import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8081", // call the service directly but in production we will use service gateway
});

export const createIncident = (data) =>
  api.post("/incidents", data);

export const getIncidents = () =>
  api.get("/incidents");