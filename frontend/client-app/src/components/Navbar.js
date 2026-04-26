import React from 'react';
import { NavLink } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import { LayoutDashboard, AlertTriangle, MessageSquare, LogOut } from 'lucide-react';

export default function Navbar() {
  const { keycloak } = useKeycloak();
  const username = keycloak.tokenParsed?.preferred_username || 'User';
  const initials = username.slice(0, 2).toUpperCase();

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <div className="navbar-logo-dot" />
        IncidentOS
      </div>
      <div className="navbar-links">
        <NavLink to="/home" className={({ isActive }) => isActive ? 'active' : ''}>
          <LayoutDashboard size={15} /> Accueil
        </NavLink>
        <NavLink to="/incidents" className={({ isActive }) => isActive ? 'active' : ''}>
          <AlertTriangle size={15} /> Mes incidents
        </NavLink>
        <NavLink to="/chat" className={({ isActive }) => isActive ? 'active' : ''}>
          <MessageSquare size={15} /> Assistant
        </NavLink>
      </div>
      <div className="navbar-right">
        <div className="navbar-user-chip">
          <div className="navbar-avatar">{initials}</div>
          {username}
        </div>
        <button className="logout-btn" onClick={() => keycloak.logout()}>
          <LogOut size={13} /> Déconnexion
        </button>
      </div>
    </nav>
  );
}