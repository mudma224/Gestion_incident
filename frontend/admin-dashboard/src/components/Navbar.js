import React, { useEffect, useRef, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import { Bell, LogOut, ChevronRight, Check, Trash2, X } from 'lucide-react';
import {
  countUnread,
  getMyNotifications,
  markAsRead,
  deleteAllMyNotifs,
} from '../api/notifications';

const breadcrumbs = {
  '/dashboard': ['Dashboard'],
  '/incidents': ['Dashboard', 'Incidents'],
  '/users':     ['Dashboard', 'Utilisateurs'],
};

const typeLabel = {
  INCIDENT_CREATED:        'Incident créé',
  INCIDENT_ASSIGNED:       'Incident assigné',
  INCIDENT_STATUS_CHANGED: 'Statut modifié',
  COMMENT_ADDED:           'Nouveau commentaire',
  INCIDENT_RESOLVED:       'Incident résolu',
  INCIDENT_CLOSED:         'Incident clôturé',
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
        {loading && (
          <div className="notif-empty">Chargement...</div>
        )}
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

function Navbar() {
  const location             = useLocation();
  const { keycloak }         = useKeycloak();
  const [count, setCount]    = useState(0);
  const [open, setOpen]      = useState(false);
  const dropdownRef          = useRef(null);

  const parts = Object.entries(breadcrumbs).find(([p]) =>
    location.pathname.startsWith(p)
  )?.[1] || ['Dashboard'];

  // Compte toutes les 30 secondes
  useEffect(() => {
    const fetch = () =>
      countUnread().then(r => setCount(r.data.count || 0)).catch(() => {});
    fetch();
    const id = setInterval(fetch, 30000);
    return () => clearInterval(id);
  }, []);

  // Ferme le dropdown si clic en dehors
  useEffect(() => {
    const handler = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const handleBellClick = () => {
    setOpen(o => !o);
    // Remet le compteur à jour à l'ouverture
    if (!open) countUnread().then(r => setCount(r.data.count || 0)).catch(() => {});
  };

  return (
    <div className="navbar">
      <div className="navbar-breadcrumb">
        {parts.map((part, i) => (
          <React.Fragment key={part}>
            {i > 0 && <ChevronRight size={13} className="navbar-divider" />}
            <span className={i === parts.length - 1 ? 'navbar-breadcrumb-current' : ''}>
              {part}
            </span>
          </React.Fragment>
        ))}
      </div>

      <div className="navbar-actions">
        <div style={{ position: 'relative' }} ref={dropdownRef}>
          <button className="icon-btn" onClick={handleBellClick}>
            <Bell size={15} />
            {count > 0 && (
              <span className="notif-badge">{count > 99 ? '99+' : count}</span>
            )}
          </button>
          {open && (
            <NotifDropdown onClose={() => setOpen(false)} />
          )}
        </div>

        <button className="logout-btn" onClick={() => keycloak.logout()}>
          <LogOut size={13} />
          Déconnexion
        </button>
      </div>
    </div>
  );
}

export default Navbar;