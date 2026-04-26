import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, AlertTriangle } from 'lucide-react';
import { getMyIncidents } from '../api/incidents';
import StatusBadge from '../components/StatusBadge';

export default function MyIncidents() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMyIncidents().then(r => setIncidents(r.data)).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="loading">Chargement...</div>;

  return (
    <div className="page">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 28 }}>
        <div>
          <div className="page-title">Mes incidents</div>
          <div className="page-subtitle">{incidents.length} incident(s) soumis</div>
        </div>
        <Link to="/incidents/new" className="btn btn-primary">
          <Plus size={14} /> Nouveau
        </Link>
      </div>

      {incidents.length === 0 ? (
        <div className="empty-state">
          <AlertTriangle size={32} style={{ margin: '0 auto 12px', display: 'block', color: 'var(--text-muted)' }} />
          Aucun incident. Créez-en un ou demandez à l'assistant.
        </div>
      ) : incidents.map(i => (
        <div key={i.id} className="incident-card">
          <div>
            <div className="incident-card-title">{i.title}</div>
            <div className="incident-card-meta">
              <span style={{ fontFamily: 'DM Mono, monospace', fontSize: 11 }}>#{i.id}</span>
              <span>·</span>
              {i.category || 'Sans catégorie'}
              <span>·</span>
              {new Date(i.createdAt).toLocaleDateString('fr-FR')}
            </div>
          </div>
          <div className="incident-card-badges">
            <StatusBadge value={i.priority} />
            <StatusBadge value={i.status} />
          </div>
        </div>
      ))}
    </div>
  );
}