import React, { useEffect, useState } from 'react';
import { Users, Search } from 'lucide-react';
import { getAllUsers } from '../api/users';

export default function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    getAllUsers().then(r => setUsers(r.data)).finally(() => setLoading(false));
  }, []);

  const filtered = users.filter(u =>
    !search || u.username?.toLowerCase().includes(search.toLowerCase()) ||
    u.email?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) return <div className="loading">Chargement...</div>;

  const roleColor = { ROLE_ADMIN: 'green', ROLE_TECHNICIEN: 'blue', ROLE_USER: 'neutral' };

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Utilisateurs</div>
          <div className="page-subtitle">{users.length} compte(s) enregistré(s)</div>
        </div>
      </div>

      <div className="controls">
        <div className="search-input">
          <Search size={14} />
          <input
            placeholder="Rechercher un utilisateur..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
      </div>

      <div className="panel">
        <table>
          <thead>
            <tr>
              <th>Utilisateur</th>
              <th>Email</th>
              <th>Prénom</th>
              <th>Nom</th>
              <th>Rôle</th>
              <th>Membre depuis</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr><td colSpan={6}>
                <div className="empty-state">Aucun utilisateur trouvé</div>
              </td></tr>
            ) : filtered.map(u => (
              <tr key={u.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{
                      width: 32, height: 32, borderRadius: '50%',
                      background: 'linear-gradient(135deg,var(--accent-blue),var(--accent-purple))',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      fontSize: 12, fontWeight: 700, color: '#fff', flexShrink: 0
                    }}>
                      {(u.username||'?').slice(0,2).toUpperCase()}
                    </div>
                    <span style={{ fontWeight: 500 }}>{u.username}</span>
                  </div>
                </td>
                <td style={{ color: 'var(--text-secondary)' }}>{u.email}</td>
                <td>{u.firstName || '—'}</td>
                <td>{u.lastName || '—'}</td>
                <td>
                  <span className={`badge badge-${roleColor[u.role] || 'ferme'}`}>
                    <span className="badge-dot" />
                    {u.role?.replace('ROLE_', '') || '—'}
                  </span>
                </td>
                <td className="td-mono">
                  {u.createdAt ? new Date(u.createdAt).toLocaleDateString('fr-FR') : '—'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}