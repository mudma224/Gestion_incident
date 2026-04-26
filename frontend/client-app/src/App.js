import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import MyIncidents from './pages/MyIncidents';
import CreateIncident from './pages/CreateIncident';
import Chat from './pages/Chat';

function ProtectedRoute({ children }) {
  const { keycloak, initialized } = useKeycloak();
  if (!initialized) return <div className="loading">Chargement...</div>;
  if (!keycloak.authenticated) { keycloak.login(); return null; }
  return children;
}

function App() {
  return (
    <BrowserRouter>
      <ProtectedRoute>
        <Navbar />
        <Routes>
          <Route path="/" element={<Navigate to="/home" replace />} />
          <Route path="/home" element={<Home />} />
          <Route path="/incidents" element={<MyIncidents />} />
          <Route path="/incidents/new" element={<CreateIncident />} />
          <Route path="/chat" element={<Chat />} />
        </Routes>
      </ProtectedRoute>
    </BrowserRouter>
  );
}

export default App;