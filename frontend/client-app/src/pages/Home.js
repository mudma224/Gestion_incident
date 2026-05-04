import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import { Plus, MessageSquare } from 'lucide-react';
import { getMyIncidents } from '../api/incidents';
import StatusBadge from '../components/StatusBadge';

export default function Home() {
  const { keycloak } = useKeycloak();
  const [incidents, setIncidents] = useState([]);
  const username = keycloak.tokenParsed?.preferred_username;

  useEffect(() => {
    getMyIncidents().then(r => setIncidents(r.data)).catch(() => {});
  }, []);

  const byStatus = (s) => incidents.filter(i => i.status === s).length;

  return (
    <div className="page">
      <div style={{ marginBottom: 28 }}>
        <div className="page-title">Bonjour, {username}</div>
        <div className="page-subtitle">Bienvenue sur votre espace de support informatique</div>
      </div>

      <div className="cards-grid">
        <div className="stat-card">
          <div className="stat-card-label">Total incidents</div>
          <div className="stat-value blue">{incidents.length}</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-label">En cours</div>
          <div className="stat-value orange">
            {byStatus('EN_COURS') + byStatus('ASSIGNE') + byStatus('NOUVEAU')}
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-card-label">Résolus</div>
          <div className="stat-value green">
            {byStatus('RESOLU') + byStatus('FERME')}
          </div>
        </div>
      </div>

      <div style={{ display: 'flex', gap: 12, marginBottom: 32 }}>
        <Link to="/incidents/new" className="btn btn-primary">
          <Plus size={15} /> Créer un incident
        </Link>
        <Link to="/chat" className="btn btn-secondary">
          <MessageSquare size={15} /> Demander à l'assistant
        </Link>
      </div>

      <div style={{ background: 'var(--bg-surface)', border: '1px solid var(--border)',
        borderRadius: 'var(--radius-lg)', padding: 20 }}>
        <div style={{ fontSize: 13, fontWeight: 600, marginBottom: 16 }}>Incidents récents</div>
        {incidents.length === 0 ? (
          <div className="empty-state" style={{ padding: '24px 0' }}>Aucun incident créé</div>
        ) : incidents.slice(0, 4).map((i, idx) => (
          <div key={i.id} style={{
            display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            padding: '11px 0',
            borderBottom: idx < Math.min(3, incidents.length - 1) ? '1px solid var(--border)' : 'none'
          }}>
            <div>
              <div style={{ fontSize: 13, fontWeight: 500 }}>{i.title}</div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>
                #{i.id} · {new Date(i.createdAt).toLocaleDateString('fr-FR')}
              </div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <StatusBadge value={i.priority} />
              <StatusBadge value={i.status} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}