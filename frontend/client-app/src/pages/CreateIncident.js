import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Send } from 'lucide-react';
import { createIncident } from '../api/incidents';

export default function CreateIncident() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ title: '', description: '', priority: 'MOYEN', category: 'AUTRE' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim()) { setError('Le titre est requis.'); return; }
    setLoading(true);
    try {
      await createIncident(form);
      navigate('/incidents');
    } catch { setError("Erreur lors de la création."); }
    finally { setLoading(false); }
  };

  return (
    <div className="page">
      <button className="back-btn" onClick={() => navigate('/incidents')}>
        <ArrowLeft size={14} /> Retour
      </button>

      <div style={{ marginBottom: 24 }}>
        <div className="page-title">Créer un incident</div>
        <div className="page-subtitle">Décrivez votre problème avec le plus de détails possible</div>
      </div>

      <div className="form-panel">
        {error && (
          <div style={{ background: 'var(--accent-red-dim)', border: '1px solid rgba(244,63,94,0.2)',
            color: 'var(--accent-red)', padding: '10px 14px', borderRadius: 'var(--radius-sm)',
            fontSize: 13, marginBottom: 20 }}>
            {error}
          </div>
        )}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Titre *</label>
            <input name="title" value={form.title} onChange={handleChange}
              placeholder="Ex : Mon imprimante ne fonctionne plus" />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea name="description" value={form.description} onChange={handleChange}
              rows={4} placeholder="Décrivez le problème en détail, ce que vous avez déjà essayé..." />
          </div>
          <div className="form-grid">
            <div className="form-group">
              <label>Priorité</label>
              <select name="priority" value={form.priority} onChange={handleChange}>
                <option value="FAIBLE">Faible</option>
                <option value="MOYEN">Moyen</option>
                <option value="ELEVE">Élevé</option>
                <option value="CRITIQUE">Critique</option>
              </select>
            </div>
            <div className="form-group">
              <label>Catégorie</label>
              <select name="category" value={form.category} onChange={handleChange}>
                <option value="MATERIEL">Matériel</option>
                <option value="LOGICIEL">Logiciel</option>
                <option value="RESEAU">Réseau</option>
                <option value="SECURITE">Sécurité</option>
                <option value="AUTRE">Autre</option>
              </select>
            </div>
          </div>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            <Send size={13} />
            {loading ? 'Envoi en cours...' : 'Soumettre l\'incident'}
          </button>
        </form>
      </div>
    </div>
  );
}