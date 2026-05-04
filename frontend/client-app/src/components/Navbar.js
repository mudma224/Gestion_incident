import React, { useEffect, useRef, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import {
  LayoutDashboard, AlertTriangle, MessageSquare,
  LogOut, Bell, Check, Trash2, X,
} from 'lucide-react';
import {
  countUnread,
  getMyNotifications,
  markAsRead,
  deleteAllMyNotifs,
} from '../api/notifications';

const typeLabel = {
  INCIDENT_CREATED:        'Incident créé',
  INCIDENT_ASSIGNED:       'Assigné',
  INCIDENT_STATUS_CHANGED: 'Statut modifié',
  COMMENT_ADDED:           'Commentaire',
  INCIDENT_RESOLVED:       'Résolu',
  INCIDENT_CLOSED:         'Clôturé',
  SYSTEM:                  'Système',
};

function NotifDropdown({ onClose }) {
  const [notifs, setNotifs]   = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () =>
    getMyNotifications()
      .then(r => setNotifs(r.data))
      .finally(() => setLoading(false));

  useEffect(() => { load(); }, []);

  const handleRead = async (id) => {
    await markAsRead(id);
    setNotifs(n => n.map(x => x.id === id ? { ...x, read: true } : x));
  };

  const handleDeleteAll = async () => {
    await deleteAllMyNotifs();
    setNotifs([]);
  };

  return (
    <div className="notif-dropdown">
      <div className="notif-dropdown-header">
        <span className="notif-dropdown-title">Notifications</span>
        <div style={{ display: 'flex', gap: 6 }}>
          {notifs.length > 0 && (
            <button className="notif-icon-action" onClick={handleDeleteAll} title="Tout supprimer">
              <Trash2 size={13} />
            </button>
          )}
          <button className="notif-icon-action" onClick={onClose}>
            <X size={13} />
          </button>
        </div>
      </div>

      <div className="notif-list">
        {loading && <div className="notif-empty">Chargement...</div>}
        {!loading && notifs.length === 0 && (
          <div className="notif-empty">Aucune notification</div>
        )}
        {notifs.map(n => (
          <div key={n.id} className={`notif-item ${n.read ? 'read' : 'unread'}`}>
            <div className="notif-item-body">
              <div className="notif-item-type">{typeLabel[n.type] || n.type}</div>
              <div className="notif-item-subject">{n.subject}</div>
              <div className="notif-item-message">{n.message}</div>
              <div className="notif-item-time">
                {new Date(n.createdAt).toLocaleString('fr-FR', {
                  day: '2-digit', month: 'short',
                  hour: '2-digit', minute: '2-digit',
                })}
              </div>
            </div>
            {!n.read && (
              <button
                className="notif-read-btn"
                onClick={() => handleRead(n.id)}
                title="Marquer comme lu"
              >
                <Check size={12} />
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default function Navbar() {
  const { keycloak }         = useKeycloak();
  const username             = keycloak.tokenParsed?.preferred_username || 'User';
  const initials             = username.slice(0, 2).toUpperCase();
  const [count, setCount]    = useState(0);
  const [open, setOpen]      = useState(false);
  const dropdownRef          = useRef(null);

  useEffect(() => {
    const fetch = () =>
      countUnread().then(r => setCount(r.data.count || 0)).catch(() => {});
    fetch();
    const id = setInterval(fetch, 30000);
    return () => clearInterval(id);
  }, []);

  useEffect(() => {
    const handler = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <div className="navbar-logo-dot" />
        IncidentOS
      </div>

      <div className="navbar-links">
        <NavLink to="/home"      className={({ isActive }) => isActive ? 'active' : ''}>
          <LayoutDashboard size={15} /> Accueil
        </NavLink>
        <NavLink to="/incidents" className={({ isActive }) => isActive ? 'active' : ''}>
          <AlertTriangle size={15} /> Mes incidents
        </NavLink>
        <NavLink to="/chat"      className={({ isActive }) => isActive ? 'active' : ''}>
          <MessageSquare size={15} /> Assistant
        </NavLink>
      </div>

      <div className="navbar-right">
        <div style={{ position: 'relative' }} ref={dropdownRef}>
          <button
            className="logout-btn"
            style={{ border: '1px solid var(--border)', padding: '6px 10px' }}
            onClick={() => setOpen(o => !o)}
          >
            <Bell size={14} />
            {count > 0 && (
              <span className="notif-badge">{count > 99 ? '99+' : count}</span>
            )}
          </button>
          {open && <NotifDropdown onClose={() => setOpen(false)} />}
        </div>

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