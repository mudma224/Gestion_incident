import React, { useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import IncidentList from './pages/IncidentList';
import IncidentDetail from './pages/IncidentDetail';
import UserList from './pages/UserList';
import { syncCurrentUser } from './api/profile';

function AccessDenied() {
  const { keycloak } = useKeycloak();

  return (
    <div className="loading" style={{ minHeight: '100vh', flexDirection: 'column', gap: 16 }}>
      <div>Acces reserve aux administrateurs.</div>
      <button className="btn btn-primary" onClick={() => keycloak.logout()}>
        Se deconnecter
      </button>
    </div>
  );
}

function AuthBootstrap({ children }) {
  const { keycloak, initialized } = useKeycloak();

  useEffect(() => {
    if (initialized && keycloak.authenticated) {
      syncCurrentUser().catch(() => {});
    }
  }, [initialized, keycloak.authenticated]);

  return children;
}

function ProtectedRoute({ children }) {
  const { keycloak, initialized } = useKeycloak();
  const roles = keycloak.tokenParsed?.realm_access?.roles || [];
  if (!initialized) return <div className="loading">Chargement...</div>;
  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }
  if (!roles.includes('ROLE_ADMIN')) {
    return <AccessDenied />;
  }
  return children;
}

function App() {
  return (
    <BrowserRouter>
      <AuthBootstrap>
        <Routes>
          <Route path="/" element={
            <ProtectedRoute>
              <Layout />
            </ProtectedRoute>
          }>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="incidents" element={<IncidentList />} />
            <Route path="incidents/:id" element={<IncidentDetail />} />
            <Route path="users" element={<UserList />} />
          </Route>
        </Routes>
      </AuthBootstrap>
    </BrowserRouter>
  );
}

export default App;
