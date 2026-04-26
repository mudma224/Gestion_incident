import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Search, Trash2, Eye, Filter } from 'lucide-react';
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
    getAllIncidents().then(r => setIncidents(r.data)).finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Supprimer cet incident ?')) return;
    await deleteIncident(id);
    load();
  };

  const filtered = incidents.filter(i => {
    if (filterStatus && i.status !== filterStatus) return false;
    if (filterPriority && i.priority !== filterPriority) return false;
    if (search && !i.title.toLowerCase().includes(search.toLowerCase())) return false;
    return true;
  });

  if (loading) return <div className="loading">Chargement...</div>;

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
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <Filter size={14} style={{ color: 'var(--text-muted)' }} />
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
          <option value="">Tous les statuts</option>
          <option value="NOUVEAU">Nouveau</option>
          <option value="ASSIGNE">Assigné</option>
          <option value="EN_COURS">En cours</option>
          <option value="RESOLU">Résolu</option>
          <option value="FERME">Fermé</option>
        </select>
        <select value={filterPriority} onChange={e => setFilterPriority(e.target.value)}>
          <option value="">Toutes priorités</option>
          <option value="FAIBLE">Faible</option>
          <option value="MOYEN">Moyen</option>
          <option value="ELEVE">Élevé</option>
          <option value="CRITIQUE">Critique</option>
        </select>
      </div>

      <div className="panel">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Titre</th>
              <th>Catégorie</th>
              <th>Priorité</th>
              <th>Statut</th>
              <th>Date de création</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr><td colSpan={7}>
                <div className="empty-state">Aucun incident trouvé</div>
              </td></tr>
            ) : filtered.map(i => (
              <tr key={i.id}>
                <td className="td-mono">#{i.id}</td>
                <td>
                  <Link to={`/incidents/${i.id}`}
                    style={{ color: 'var(--text-primary)', fontWeight: 500 }}>
                    {i.title}
                  </Link>
                </td>
                <td style={{ color: 'var(--text-secondary)' }}>{i.category || '—'}</td>
                <td><StatusBadge value={i.priority} /></td>
                <td><StatusBadge value={i.status} /></td>
                <td className="td-mono">
                  {new Date(i.createdAt).toLocaleDateString('fr-FR')}
                </td>
                <td>
                  <div style={{ display: 'flex', gap: 8 }}>
                    <Link to={`/incidents/${i.id}`} className="btn btn-ghost btn-sm">
                      <Eye size={12} />
                    </Link>
                    <button className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(i.id)}>
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