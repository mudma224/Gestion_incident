import React, { useState, useEffect } from 'react';
import { getIncidents, createIncident } from './services/api';
import CommentList from './services/CommentList'; 
import { AlertCircle, Plus, X } from 'lucide-react';

function App() {
  const [incidents, setIncidents] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [newIncident, setNewIncident] = useState({ titre: '', description: '', priorite: 'MOYENNE' });
  const [selectedIncident, setSelectedIncident] = useState(null);

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    try {
      const response = await getIncidents();
      setIncidents(response.data);
    } catch (error) {
      console.error("Erreur de récupération:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createIncident(newIncident);
      setNewIncident({ titre: '', description: '', priorite: 'MOYENNE' });
      setShowForm(false);
      fetchData();
      alert("Incident créé avec succès !");
    } catch (error) {
      alert("Erreur création : " + (error.response?.data?.message || error.message));
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewIncident(prev => ({ ...prev, [name]: value }));
  };

  return (
    <div style={{ padding: '30px', maxWidth: '1300px', margin: '0 auto', fontFamily: 'system-ui' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h1 style={{ display: 'flex', alignItems: 'center', gap: '10px', margin: 0 }}>
          <AlertCircle color="#e11d48" /> MVP Incidents
        </h1>
        <button onClick={() => setShowForm(!showForm)} style={{ backgroundColor: '#2563eb', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
          {showForm ? <X size={18} /> : <Plus size={18} />}
          {showForm ? 'Annuler' : 'Nouvel Incident'}
        </button>
      </header>

      {showForm && (
        /* Formulaire identique à la version précédente – je te le remets si tu veux, mais tu l'as déjà */
        <form onSubmit={handleSubmit} style={{ backgroundColor: '#f9fafb', padding: '25px', borderRadius: '12px', marginBottom: '30px', border: '1px solid #e5e7eb' }}>
          {/* ... même formulaire que précédemment ... */}
          <div style={{ display: 'flex', gap: '15px' }}>
            <button type="submit" style={{ backgroundColor: '#059669', color: 'white', border: 'none', padding: '12px 24px', borderRadius: '8px', cursor: 'pointer' }}>Enregistrer</button>
            <button type="button" onClick={() => setShowForm(false)} style={{ backgroundColor: '#64748b', color: 'white', border: 'none', padding: '12px 24px', borderRadius: '8px', cursor: 'pointer' }}>Annuler</button>
          </div>
        </form>
      )}

      {/* Liste des incidents */}
      <div style={{ backgroundColor: 'white', borderRadius: '12px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f3f4f6' }}>
            <tr>
              <th style={{ padding: '18px', textAlign: 'left' }}>Incidents</th>
              <th style={{ padding: '18px', textAlign: 'left' }}>Priorité</th>
              <th style={{ padding: '18px', textAlign: 'left' }}>Statut</th>
            </tr>
          </thead>
          <tbody>
            {incidents.map(inc => (
              <tr key={inc.id} onClick={() => setSelectedIncident(inc)} style={{ borderTop: '1px solid #e5e7eb', cursor: 'pointer', backgroundColor: selectedIncident?.id === inc.id ? '#eff6ff' : 'transparent' }}>
                <td style={{ padding: '18px' }}>
                  <strong>{inc.titre}</strong>
                  <p style={{ margin: '6px 0 0 0', fontSize: '0.9em', color: '#666' }}>{inc.description}</p>
                </td>
                <td style={{ padding: '18px' }}>
                  <span style={{ padding: '4px 12px', borderRadius: '9999px', fontSize: '0.8em', backgroundColor: inc.priorite === 'HAUTE' ? '#fee2e2' : inc.priorite === 'MOYENNE' ? '#fefce8' : '#ecfdf5', color: inc.priorite === 'HAUTE' ? '#b91c1c' : inc.priorite === 'MOYENNE' ? '#854d0e' : '#0f766e' }}>
                    {inc.priorite}
                  </span>
                </td>
                <td style={{ padding: '18px' }}><span style={{ color: '#059669', fontWeight: '600' }}>{inc.statut || 'NOUVEAU'}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* === PANNEAU DÉTAILS + COMMENTAIRES CÔTE À CÔTE === */}
      {selectedIncident && (
        <div style={{ marginTop: '30px', backgroundColor: '#fff', padding: '25px', borderRadius: '12px', border: '1px solid #e5e7eb', boxShadow: '0 4px 6px -1px rgba(0,0,0,0.1)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
            <h3 style={{ margin: 0 }}>Détails de l'incident</h3>
            <X size={22} style={{ cursor: 'pointer' }} onClick={() => setSelectedIncident(null)} />
          </div>

          <div style={{ display: 'flex', gap: '40px' }}>
            {/* Colonne gauche : Infos */}
            <div style={{ flex: '1' }}>
              <p><strong>Titre :</strong> {selectedIncident.titre}</p>
              <p><strong>Description :</strong> {selectedIncident.description}</p>
              <p><strong>Priorité :</strong> {selectedIncident.priorite}</p>
              <p><strong>Statut :</strong> {selectedIncident.statut}</p>
            </div>

            {/* Colonne droite : Commentaires */}
            <div style={{ flex: '1', borderLeft: '2px solid #e5e7eb', paddingLeft: '30px' }}>
              <h4 style={{ marginBottom: '15px' }}>Échanges & Commentaires</h4>
              <CommentList incidentId={selectedIncident.id} />
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;