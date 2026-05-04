import React, { useEffect, useState } from 'react';
import { Search } from 'lucide-react';
import { getAllUsers } from '../api/users';

export default function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');

  useEffect(() => {
    getAllUsers()
      .then(response => setUsers(response.data))
      .finally(() => setLoading(false));
  }, []);

  const filtered = users.filter(user =>
    !search
    || user.username?.toLowerCase().includes(search.toLowerCase())
    || user.email?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) {
    return <div className="loading">Chargement...</div>;
  }

  const roleColor = { ROLE_ADMIN: 'green', ROLE_TECHNICIEN: 'blue', ROLE_USER: 'neutral' };

  return (
    <div>
      <div className="page-header">
        <div>
          <div className="page-title">Utilisateurs</div>
          <div className="page-subtitle">{users.length} compte(s) enregistre(s)</div>
        </div>
      </div>

      <div className="controls">
        <div className="search-input">
          <Search size={14} />
          <input
            placeholder="Rechercher un utilisateur..."
            value={search}
            onChange={event => setSearch(event.target.value)}
          />
        </div>
      </div>

      <div className="panel">
        <table>
          <thead>
            <tr>
              <th>Utilisateur</th>
              <th>Email</th>
              <th>Prenom</th>
              <th>Nom</th>
              <th>Role</th>
              <th>Membre depuis</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr><td colSpan={6}><div className="empty-state">Aucun utilisateur trouve</div></td></tr>
            ) : filtered.map(user => (
              <tr key={user.id}>
                <td>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div
                      style={{
                        width: 32,
                        height: 32,
                        borderRadius: '50%',
                        background: 'linear-gradient(135deg,var(--accent-blue),var(--accent-purple))',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: 12,
                        fontWeight: 700,
                        color: '#fff',
                        flexShrink: 0,
                      }}
                    >
                      {(user.username || '?').slice(0, 2).toUpperCase()}
                    </div>
                    <span style={{ fontWeight: 500 }}>{user.username}</span>
                  </div>
                </td>
                <td style={{ color: 'var(--text-secondary)' }}>{user.email}</td>
                <td>{user.firstName || '-'}</td>
                <td>{user.lastName || '-'}</td>
                <td>
                  <span className={`badge badge-${roleColor[user.role] || 'ferme'}`}>
                    <span className="badge-dot" />
                    {user.role?.replace('ROLE_', '') || '-'}
                  </span>
                </td>
                <td className="td-mono">
                  {user.createdAt ? new Date(user.createdAt).toLocaleDateString('fr-FR') : '-'}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
