import React, { useState, useEffect } from 'react';
import { getIncidents, createIncident } from './api';
import { AlertCircle, Plus, Send, X, MessageSquare } from 'lucide-react';
import CommentSection from './components/CommentSection';

function App() {
  const [incidents, setIncidents] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [newIncident, setNewIncident] = useState({ titre: '', description: '' });
  const [selectedIncidentId, setSelectedIncidentId] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await getIncidents();
      console.log("Données reçues du Backend :", response.data);
      setIncidents(response.data);
    } catch (error) {
      console.error("Erreur de récupération:", error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createIncident(newIncident);
      setNewIncident({ titre: '', description: '' });
      setShowForm(false);
      fetchData();
    } catch (error) {
      alert("Erreur lors de la création de l'incident");
    }
  };

  return (
    <div style={{ padding: '30px', maxWidth: '1000px', margin: '0 auto', fontFamily: 'system-ui' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '30px' }}>
        <h1 style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <AlertCircle color="#e11d48" /> MVP Incidents
        </h1>
        <button 
          onClick={() => setShowForm(!showForm)}
          style={{ backgroundColor: '#2563eb', color: 'white', border: 'none', padding: '8px 15px', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          {showForm ? <X size={18} /> : <Plus size={18} />}
          {showForm ? 'Annuler' : 'Nouvel Incident'}
        </button>
      </header>

      {showForm && (
        <form onSubmit={handleSubmit} style={{ backgroundColor: '#f9fafb', padding: '20px', borderRadius: '8px', marginBottom: '20px', border: '1px solid #e5e7eb' }}>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Titre</label>
            <input 
              required
              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
              value={newIncident.titre}
              onChange={(e) => setNewIncident({...newIncident, titre: e.target.value})}
              placeholder="Ex: Problème réseau..."
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Description</label>
            <textarea 
              required
              style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
              value={newIncident.description}
              onChange={(e) => setNewIncident({...newIncident, description: e.target.value})}
              placeholder="Détaillez l'incident..."
            />
          </div>
          <button type="submit" style={{ backgroundColor: '#059669', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Send size={18} /> Enregistrer l'incident
          </button>
        </form>
      )}

      <div style={{ backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f3f4f6' }}>
            <tr>
              <th style={{ padding: '15px', textAlign: 'left' }}>Titre</th>
              <th style={{ padding: '15px', textAlign: 'left' }}>Description</th>
              <th style={{ padding: '15px', textAlign: 'left' }}>Statut</th>
              <th style={{ padding: '15px', textAlign: 'left' }}>Action</th>
            </tr>
          </thead>
          <tbody>
            {incidents.map(inc => (
              <React.Fragment key={inc.id}>
                <tr style={{ borderTop: '1px solid #e5e7eb' }}>
                  <td style={{ padding: '15px' }}>{inc.titre}</td>
                  <td style={{ padding: '15px' }}>{inc.description}</td>
                  <td style={{ padding: '15px' }}>
                    <span style={{ backgroundColor: '#dcfce7', color: '#166534', padding: '4px 8px', borderRadius: '12px', fontSize: '12px' }}>
                      {inc.statut || 'OUVERT'}
                    </span>
                  </td>
                  <td style={{ padding: '15px' }}>
                    <button 
                      onClick={() => setSelectedIncidentId(selectedIncidentId === inc.id ? null : inc.id)}
                      style={{ background: 'none', border: 'none', color: '#2563eb', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '5px' }}
                    >
                      <MessageSquare size={16} /> 
                      {selectedIncidentId === inc.id ? 'Fermer' : 'Commenter'}
                    </button>
                  </td>
                </tr>
                
                {selectedIncidentId === inc.id && (
                  <tr>
                    <td colSpan="4" style={{ padding: '15px', backgroundColor: '#f8fafc' }}>
                      <CommentSection incidentId={inc.id} />
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;