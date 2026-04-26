import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  AlertTriangle, Users, CheckCircle, Clock,
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
      .then(([ir, ur]) => { setIncidents(ir.data); setUsers(ur.data); })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div className="loading"><Activity size={16} /> Chargement...</div>
  );

  const byStatus = (s) => incidents.filter(i => i.status === s).length;
  const recent = [...incidents]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 6);

  const recentActivity = recent.map(i => ({
    text: i.title,
    status: i.status,
    time: new Date(i.createdAt).toLocaleString('fr-FR', { day:'2-digit', month:'short', hour:'2-digit', minute:'2-digit' }),
    color: { NOUVEAU:'blue', ASSIGNE:'orange', EN_COURS:'orange', RESOLU:'green', FERME:'neutral' }[i.status] || 'blue',
  }));

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Vue d'ensemble</div>
          <div className="page-subtitle">
            {new Date().toLocaleDateString('fr-FR', { weekday:'long', day:'numeric', month:'long', year:'numeric' })}
          </div>
        </div>
        <Link to="/incidents/new" className="btn btn-primary">
          <AlertTriangle size={14} /> Nouvel incident
        </Link>
      </div>

      <div className="cards-grid">
        <StatCard label="Total incidents"  value={incidents.length} icon={AlertTriangle} accent="blue"   delta={`${incidents.length} au total`}   deltaDir="neutral" />
        <StatCard label="Nouveaux"         value={byStatus('NOUVEAU')}  icon={Clock}         accent="orange" delta="En attente"   deltaDir="down" />
        <StatCard label="En cours"         value={byStatus('EN_COURS') + byStatus('ASSIGNE')} icon={Activity}  accent="blue"   delta="Actifs"  deltaDir="up" />
        <StatCard label="Résolus"          value={byStatus('RESOLU') + byStatus('FERME')} icon={CheckCircle}  accent="green" delta="Clôturés" deltaDir="up" />
      </div>

      <div className="panels-grid">
        {/* Recent incidents table */}
        <div className="panel">
          <div className="panel-header">
            <div>
              <div className="panel-title">Incidents récents</div>
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
                <th>Priorité</th>
                <th>Statut</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {recent.length === 0 ? (
                <tr><td colSpan={4}>
                  <div className="empty-state">Aucun incident</div>
                </td></tr>
              ) : recent.map(i => (
                <tr key={i.id}>
                  <td>
                    <Link to={`/incidents/${i.id}`}
                      style={{ color: 'var(--text-primary)', fontWeight: 500 }}>
                      {i.title}
                    </Link>
                    <div className="td-mono" style={{ marginTop: 2 }}>#{i.id}</div>
                  </td>
                  <td><StatusBadge value={i.priority} /></td>
                  <td><StatusBadge value={i.status} /></td>
                  <td className="td-mono">
                    {new Date(i.createdAt).toLocaleDateString('fr-FR')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Activity feed */}
        <div className="panel">
          <div className="panel-header">
            <div>
              <div className="panel-title">Activité récente</div>
              <div className="panel-subtitle">Dernières mises à jour</div>
            </div>
          </div>
          <div className="activity-list">
            {recentActivity.length === 0 ? (
              <div className="empty-state">Aucune activité</div>
            ) : recentActivity.map((a, i) => (
              <div key={i} className="activity-item">
                <div className="activity-dot-wrap">
                  <div className={`activity-dot ${a.color}`} />
                  {i < recentActivity.length - 1 && <div className="activity-line" />}
                </div>
                <div className="activity-content">
                  <div className="activity-text">
                    <strong>{a.text}</strong>
                  </div>
                  <div style={{ marginTop: 4 }}>
                    <StatusBadge value={a.status} />
                  </div>
                  <div className="activity-time">{a.time}</div>
                </div>
              </div>
            ))}
          </div>

          {/* Users summary */}
          <div className="divider" style={{ margin: '0' }} />
          <div className="panel-header" style={{ borderBottom: 'none' }}>
            <div>
              <div className="panel-title">Utilisateurs</div>
              <div className="panel-subtitle">{users.length} comptes enregistrés</div>
            </div>
            <Link to="/users" className="panel-action">
              Voir <ArrowRight size={13} />
            </Link>
          </div>
          {users.slice(0, 3).map(u => (
            <div key={u.id} className="activity-item" style={{ padding: '10px 20px' }}>
              <div style={{
                width: 30, height: 30, borderRadius: '50%',
                background: 'linear-gradient(135deg,var(--accent-blue),var(--accent-purple))',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: 11, fontWeight: 700, color: '#fff', flexShrink: 0
              }}>
                {(u.username||'?').slice(0,2).toUpperCase()}
              </div>
              <div>
                <div style={{ fontSize: 13, fontWeight: 500 }}>{u.username}</div>
                <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{u.email}</div>
              </div>
              <StatusBadge value={u.role?.replace('ROLE_','')} />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}