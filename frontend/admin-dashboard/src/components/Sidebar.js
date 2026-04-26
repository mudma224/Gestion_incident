import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import {
  LayoutDashboard, AlertTriangle, Users,
  Settings, LogOut, ChevronRight
} from 'lucide-react';

function Sidebar() {
  const { keycloak } = useKeycloak();
  const navigate = useNavigate();
  const username = keycloak.tokenParsed?.preferred_username || 'Admin';
  const initials = username.slice(0, 2).toUpperCase();

  return (
    <div className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-dot" />
        IncidentOS
      </div>

      <div className="sidebar-section-label">Navigation</div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }) => isActive ? 'active' : ''}>
          <LayoutDashboard size={16} /> Dashboard
        </NavLink>
        <NavLink to="/incidents" className={({ isActive }) => isActive ? 'active' : ''}>
          <AlertTriangle size={16} /> Incidents
        </NavLink>
        <NavLink to="/users" className={({ isActive }) => isActive ? 'active' : ''}>
          <Users size={16} /> Utilisateurs
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-user" onClick={() => keycloak.logout()}>
          <div className="sidebar-avatar">{initials}</div>
          <div className="sidebar-user-info">
            <div className="sidebar-user-name">{username}</div>
            <div className="sidebar-user-role">Administrateur</div>
          </div>
          <LogOut size={14} style={{ color: 'var(--text-muted)', flexShrink: 0 }} />
        </div>
      </div>
    </div>
  );
}

export default Sidebar;