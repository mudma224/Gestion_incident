import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import IncidentList from './pages/IncidentList';
import IncidentDetail from './pages/IncidentDetail';
import UserList from './pages/UserList';

function ProtectedRoute({ children }) {
  const { keycloak, initialized } = useKeycloak();
  if (!initialized) return <div className="loading">Chargement...</div>;
  if (!keycloak.authenticated) {
    keycloak.login();
    return null;
  }
  return children;
}

function App() {
  return (
    <BrowserRouter>
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
    </BrowserRouter>
  );
}

export default App;