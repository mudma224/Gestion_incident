import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft, AlertTriangle, User, Calendar,
  MessageSquare, Send, UserCheck, RefreshCw
} from 'lucide-react';


import { getIncidentById, updateStatus, assignIncident } from '../api/incidents';
import { getCommentsByIncident, createComment } from '../api/comments';
import { getAllUsers } from '../api/users';
import StatusBadge from '../components/StatusBadge';

const WORKFLOW = ['NOUVEAU', 'ASSIGNE', 'EN_COURS', 'RESOLU', 'FERME'];

export default function IncidentDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [incident, setIncident] = useState(null);
  const [comments, setComments] = useState([]);
  const [users, setUsers] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [selectedTech, setSelectedTech] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const [ir, cr, ur] = await Promise.all([
        getIncidentById(id), getCommentsByIncident(id), getAllUsers()
      ]);
      setIncident(ir.data);
      setComments(cr.data);
      setUsers(ur.data);
    } catch { setError('Erreur de chargement.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [id]);

  const handleStatusChange = async (newStatus) => {
    try { await updateStatus(id, newStatus); load(); setError(''); }
    catch (e) { setError(e.response?.data?.message || 'Transition invalide.'); }
  };

  const handleAssign = async () => {
    if (!selectedTech) return;
    await assignIncident(id, selectedTech);
    load();
  };

  const handleComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    await createComment({ incidentId: Number(id), content: newComment });
    setNewComment('');
    load();
  };

  if (loading) return <div className="loading">Chargement...</div>;
  if (!incident) return <div className="error-msg">{error}</div>;

  const techs = users.filter(u => u.role === 'ROLE_TECHNICIEN');
  const currentStep = WORKFLOW.indexOf(incident.status);

  return (
    <div>
      <button className="back-btn" onClick={() => navigate('/incidents')}>
        <ArrowLeft size={14} /> Retour aux incidents
      </button>

      {error && (
        <div className="error-msg">
          <AlertTriangle size={14} /> {error}
        </div>
      )}

      <div className="page-header">
        <div>
          <div className="page-title">{incident.title}</div>
          <div className="page-subtitle" style={{ display: 'flex', alignItems: 'center', gap: 10, marginTop: 6 }}>
            <span className="td-mono" style={{ fontSize: 13 }}>#{incident.id}</span>
            <StatusBadge value={incident.status} />
            <StatusBadge value={incident.priority} />
          </div>
        </div>
      </div>

      <div className="detail-grid">
        {/* Left column */}
        <div>
          {/* Workflow */}
          <div className="detail-card">
            <div className="detail-card-title">
              <RefreshCw size={14} /> Statut &amp; Workflow
            </div>
            <div className="workflow-steps">
              {WORKFLOW.map((s, i) => (
                <React.Fragment key={s}>
                  <button
                    className={`workflow-step ${i === currentStep ? 'active' : ''}`}
                    style={{ cursor: i > currentStep && i === currentStep + 1 ? 'pointer' : 'default' }}
                    onClick={() => i === currentStep + 1 && handleStatusChange(s)}
                  >
                    {s}
                  </button>
                  {i < WORKFLOW.length - 1 && (
                    <span className="workflow-arrow">›</span>
                  )}
                </React.Fragment>
              ))}
            </div>
            <p style={{ fontSize: 12, color: 'var(--text-muted)' }}>
              Cliquez sur la prochaine étape pour avancer le workflow.
            </p>
          </div>

          {/* Details */}
          <div className="detail-card">
            <div className="detail-card-title">
              <AlertTriangle size={14} /> Détails de l'incident
            </div>
            <div className="detail-row">
              <div className="detail-field">
                <div className="label">Catégorie</div>
                <div className="value">{incident.category || '—'}</div>
              </div>
              <div className="detail-field">
                <div className="label">Créé le</div>
                <div className="value td-mono" style={{ fontSize: 13 }}>
                  {new Date(incident.createdAt).toLocaleString('fr-FR')}
                </div>
              </div>
              <div className="detail-field">
                <div className="label">Assigné à</div>
                <div className="value">{incident.assignedToKeycloakId || 'Non assigné'}</div>
              </div>
            </div>
            {incident.description && (
              <div>
                <div style={{ fontSize: 11, fontWeight: 600, color: 'var(--text-muted)',
                  textTransform: 'uppercase', letterSpacing: '0.5px', marginBottom: 8 }}>
                  Description
                </div>
                <p style={{ fontSize: 14, color: 'var(--text-primary)', lineHeight: 1.7 }}>
                  {incident.description}
                </p>
              </div>
            )}
          </div>

          {/* Comments */}
          <div className="detail-card">
            <div className="detail-card-title">
              <MessageSquare size={14} /> Commentaires ({comments.length})
            </div>

            {comments.length === 0 ? (
              <div className="empty-state" style={{ padding: '20px 0' }}>
                Aucun commentaire
              </div>
            ) : comments.map(c => (
              <div key={c.id} className="comment-item">
                <div className="comment-meta">
                  <User size={11} />
                  {c.authorKeycloakId}
                  <span>·</span>
                  {new Date(c.createdAt).toLocaleString('fr-FR')}
                </div>
                <div className="comment-text">{c.content}</div>
              </div>
            ))}

            <div style={{ marginTop: 16 }}>
              <form onSubmit={handleComment} style={{ display: 'flex', gap: 10 }}>
                <div className="form-group" style={{ flex: 1, marginBottom: 0 }}>
                  <input
                    value={newComment}
                    onChange={e => setNewComment(e.target.value)}
                    placeholder="Ajouter un commentaire..."
                  />
                </div>
                <button type="submit" className="btn btn-primary">
                  <Send size={13} />
                </button>
              </form>
            </div>
          </div>
        </div>

        {/* Right column */}
        <div>
          <div className="detail-card">
            <div className="detail-card-title">
              <UserCheck size={14} /> Assigner un technicien
            </div>
            <div className="form-group">
              <label>Technicien</label>
              <select value={selectedTech} onChange={e => setSelectedTech(e.target.value)}>
                <option value="">Sélectionner...</option>
                {techs.map(t => (
                  <option key={t.keycloakId} value={t.keycloakId}>
                    {t.username}
                  </option>
                ))}
              </select>
            </div>
            <button className="btn btn-primary" style={{ width: '100%' }}
              onClick={handleAssign}>
              <UserCheck size={13} /> Assigner
            </button>
          </div>

          <div className="detail-card">
            <div className="detail-card-title">
              <Calendar size={14} /> Chronologie
            </div>
            <div style={{ fontSize: 13 }}>
              <div style={{ marginBottom: 12 }}>
                <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase',
                  letterSpacing: '0.5px', marginBottom: 4 }}>Créé le</div>
                <div className="td-mono">{new Date(incident.createdAt).toLocaleString('fr-FR')}</div>
              </div>
              {incident.updatedAt && (
                <div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)', textTransform: 'uppercase',
                    letterSpacing: '0.5px', marginBottom: 4 }}>Dernière modification</div>
                  <div className="td-mono">{new Date(incident.updatedAt).toLocaleString('fr-FR')}</div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}