import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Search, Trash2, Eye, Filter } from 'lucide-react';
import { getAllIncidents, deleteIncident } from '../api/incidents';
import StatusBadge from '../components/StatusBadge';

export default function IncidentList() {
  const [incidents, setIncidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState('');
  const [filterPriority, setFilterPriority] = useState('');
  const [search, setSearch] = useState('');

  const load = () => {
    setLoading(true);
    getAllIncidents()
      .then(response => setIncidents(response.data))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const handleDelete = async id => {
    if (!window.confirm('Supprimer cet incident ?')) {
      return;
    }
    await deleteIncident(id);
    load();
  };

  const filtered = incidents.filter(incident => {
    if (filterStatus && incident.status !== filterStatus) {
      return false;
    }
    if (filterPriority && incident.priority !== filterPriority) {
      return false;
    }
    if (search && !incident.title.toLowerCase().includes(search.toLowerCase())) {
      return false;
    }
    return true;
  });

  if (loading) {
    return <div className="loading">Chargement...</div>;
  }

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Incidents</div>
          <div className="page-subtitle">{incidents.length} incident(s) au total</div>
        </div>
      </div>

      <div className="controls">
        <div className="search-input">
          <Search size={14} />
          <input
            placeholder="Rechercher..."
            value={search}
            onChange={event => setSearch(event.target.value)}
          />
        </div>
        <Filter size={14} style={{ color: 'var(--text-muted)' }} />
        <select value={filterStatus} onChange={event => setFilterStatus(event.target.value)}>
          <option value="">Tous les statuts</option>
          <option value="NOUVEAU">Nouveau</option>
          <option value="ASSIGNE">Assigne</option>
          <option value="EN_COURS">En cours</option>
          <option value="RESOLU">Resolu</option>
          <option value="FERME">Ferme</option>
        </select>
        <select value={filterPriority} onChange={event => setFilterPriority(event.target.value)}>
          <option value="">Toutes priorites</option>
          <option value="FAIBLE">Faible</option>
          <option value="MOYEN">Moyen</option>
          <option value="ELEVE">Eleve</option>
          <option value="CRITIQUE">Critique</option>
        </select>
      </div>

      <div className="panel">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Titre</th>
              <th>Categorie</th>
              <th>Priorite</th>
              <th>Statut</th>
              <th>Date de creation</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr><td colSpan={7}><div className="empty-state">Aucun incident trouve</div></td></tr>
            ) : filtered.map(incident => (
              <tr key={incident.id}>
                <td className="td-mono">#{incident.id}</td>
                <td>
                  <Link to={`/incidents/${incident.id}`} style={{ color: 'var(--text-primary)', fontWeight: 500 }}>
                    {incident.title}
                  </Link>
                </td>
                <td style={{ color: 'var(--text-secondary)' }}>{incident.category || '-'}</td>
                <td><StatusBadge value={incident.priority} /></td>
                <td><StatusBadge value={incident.status} /></td>
                <td className="td-mono">{new Date(incident.createdAt).toLocaleDateString('fr-FR')}</td>
                <td>
                  <div style={{ display: 'flex', gap: 8 }}>
                    <Link to={`/incidents/${incident.id}`} className="btn btn-ghost btn-sm">
                      <Eye size={12} />
                    </Link>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(incident.id)}>
                      <Trash2 size={12} />
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
