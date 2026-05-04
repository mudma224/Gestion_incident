import axios from 'axios';
import keycloak from '../keycloak';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// Avant chaque requête : refresh proactif si le token expire dans moins de 30s
api.interceptors.request.use(async (config) => {
  try {
    await keycloak.updateToken(30);
  } catch {
    keycloak.login();
    return Promise.reject('Session expirée');
  }

  if (keycloak.token) {
    config.headers.Authorization = `Bearer ${keycloak.token}`;
  }
  return config;
});

// Après chaque réponse : si 401 malgré tout, rejouer la requête une fois
api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        await keycloak.updateToken(60);
        originalRequest.headers.Authorization = `Bearer ${keycloak.token}`;
        return api(originalRequest); // rejoue la requête
      } catch {
        keycloak.login();
      }
    }
    return Promise.reject(error);
  }
);

export default api;