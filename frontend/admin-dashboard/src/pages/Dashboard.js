import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  AlertTriangle, CheckCircle, Clock,
  TrendingUp, TrendingDown, ArrowRight, Activity
} from 'lucide-react';
import { getAllIncidents } from '../api/incidents';
import { getAllUsers } from '../api/users';
import StatusBadge from '../components/StatusBadge';

function StatCard({ label, value, icon: Icon, accent, delta, deltaDir }) {
  return (
    <div className="stat-card">
      <div className="stat-card-header">
        <div className="stat-card-label">{label}</div>
        <div className={`stat-icon ${accent}`}>
          <Icon size={16} />
        </div>
      </div>
      <div className="stat-value">{value}</div>
      {delta && (
        <div className={`stat-delta ${deltaDir}`}>
          {deltaDir === 'up' ? <TrendingUp size={11} /> : <TrendingDown size={11} />}
          {delta}
        </div>
      )}
    </div>
  );
}

export default function Dashboard() {
  const [incidents, setIncidents] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getAllIncidents(), getAllUsers()])
      .then(([incidentResponse, userResponse]) => {
        setIncidents(incidentResponse.data);
        setUsers(userResponse.data);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div className="loading"><Activity size={16} /> Chargement...</div>;
  }

  const byStatus = status => incidents.filter(incident => incident.status === status).length;
  const recent = [...incidents]
    .sort((left, right) => new Date(right.createdAt) - new Date(left.createdAt))
    .slice(0, 6);

  const recentActivity = recent.map(incident => ({
    text: incident.title,
    status: incident.status,
    time: new Date(incident.createdAt).toLocaleString('fr-FR', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit',
    }),
    color: {
      NOUVEAU: 'blue',
      ASSIGNE: 'orange',
      EN_COURS: 'orange',
      RESOLU: 'green',
      FERME: 'neutral',
    }[incident.status] || 'blue',
  }));

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Vue d'ensemble</div>
          <div className="page-subtitle">
            {new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
          </div>
        </div>
      </div>

      <div className="cards-grid">
        <StatCard label="Total incidents" value={incidents.length} icon={AlertTriangle} accent="blue" delta={`${incidents.length} au total`} deltaDir="neutral" />
        <StatCard label="Nouveaux" value={byStatus('NOUVEAU')} icon={Clock} accent="orange" delta="En attente" deltaDir="down" />
        <StatCard label="En cours" value={byStatus('EN_COURS') + byStatus('ASSIGNE')} icon={Activity} accent="blue" delta="Actifs" deltaDir="up" />
        <StatCard label="Resolus" value={byStatus('RESOLU') + byStatus('FERME')} icon={CheckCircle} accent="green" delta="Clotures" deltaDir="up" />
      </div>

      <div className="panels-grid">
        <div className="panel">
          <div className="panel-header">
            <div>
              <div className="panel-title">Incidents recents</div>
              <div className="panel-subtitle">Les {recent.length} derniers incidents</div>
            </div>
            <Link to="/incidents" className="panel-action">
              Voir tout <ArrowRight size={13} />
            </Link>
          </div>
          <table>
            <thead>
              <tr>
                <th>Incident</th>
                <th>Priorite</th>
                <th>Statut</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {recent.length === 0 ? (
                <tr><td colSpan={4}><div className="empty-state">Aucun incident</div></td></tr>
              ) : recent.map(incident => (
                <tr key={incident.id}>
                  <td>
                    <Link to={`/incidents/${incident.id}`} style={{ color: 'var(--text-primary)', fontWeight: 500 }}>
                      {incident.title}
                    </Link>
                    <div className="td-mono" style={{ marginTop: 2 }}>#{incident.id}</div>
                  </td>
                  <td><StatusBadge value={incident.priority} /></td>
                  <td><StatusBadge value={incident.status} /></td>
                  <td className="td-mono">{new Date(incident.createdAt).toLocaleDateString('fr-FR')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="panel">
          <div className="panel-header">
            <div>
              <div className="panel-title">Activite recente</div>
              <div className="panel-subtitle">Dernieres mises a jour</div>
            </div>
          </div>
          <div className="activity-list">
            {recentActivity.length === 0 ? (
              <div className="empty-state">Aucune activite</div>
            ) : recentActivity.map((activity, index) => (
              <div key={index} className="activity-item">
                <div className="activity-dot-wrap">
                  <div className={`activity-dot ${activity.color}`} />
                  {index < recentActivity.length - 1 && <div className="activity-line" />}
                </div>
                <div className="activity-content">
                  <div className="activity-text">
                    <strong>{activity.text}</strong>
                  </div>
                  <div style={{ marginTop: 4 }}>
                    <StatusBadge value={activity.status} />
                  </div>
                  <div className="activity-time">{activity.time}</div>
                </div>
              </div>
            ))}
          </div>

          <div className="divider" style={{ margin: '0' }} />
          <div className="panel-header" style={{ borderBottom: 'none' }}>
            <div>
              <div className="panel-title">Utilisateurs</div>
              <div className="panel-subtitle">{users.length} comptes enregistres</div>
            </div>
            <Link to="/users" className="panel-action">
              Voir <ArrowRight size={13} />
            </Link>
          </div>
          {users.slice(0, 3).map(user => (
            <div key={user.id} className="activity-item" style={{ padding: '10px 20px' }}>
              <div
                style={{
                  width: 30,
                  height: 30,
                  borderRadius: '50%',
                  background: 'linear-gradient(135deg,var(--accent-blue),var(--accent-purple))',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: 11,
                  fontWeight: 700,
                  color: '#fff',
                  flexShrink: 0,
                }}
              >
                {(user.username || '?').slice(0, 2).toUpperCase()}
              </div>
              <div>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{user.username}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{user.email}</div>
              </div>
              <StatusBadge value={user.role?.replace('ROLE_', '')} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
