import React, { useState, useEffect } from 'react';
import { getIncidents, createIncident } from './services/api';
import CommentList from './services/CommentList'; // Import du nouveau composant
import { AlertCircle, Plus, Send, X } from 'lucide-react';

function App() {
  const [incidents, setIncidents] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [newIncident, setNewIncident] = useState({ titre: '', description: '' });
  const [selectedIncident, setSelectedIncident] = useState(null); // État pour la sélection

  useEffect(() => {
    fetchData();
  }, []);

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
      setNewIncident({ titre: '', description: '' });
      setShowForm(false);
      fetchData();
    } catch (error) {
      alert("Erreur lors de la création");
    }
  };

  return (
    <div style={{ padding: '30px', maxWidth: '900px', margin: '0 auto', fontFamily: 'system-ui' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '30px' }}>
        <h1><AlertCircle color="#e11d48" /> MVP Incidents</h1>
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
          {/* ... Champs Titre et Description (identiques à ton code) ... */}
          <div style={{ marginBottom: '15px' }}>
             <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Titre</label>
             <input required style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
               value={newIncident.titre} onChange={(e) => setNewIncident({...newIncident, titre: e.target.value})} />
          </div>
          <div style={{ marginBottom: '15px' }}>
             <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>Description</label>
             <textarea required style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
               value={newIncident.description} onChange={(e) => setNewIncident({...newIncident, description: e.target.value})} />
          </div>
          <button type="submit" style={{ backgroundColor: '#059669', color: 'white', border: 'none', padding: '10px 20px', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Send size={18} /> Enregistrer
          </button>
        </form>
      )}

      <div style={{ backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f3f4f6' }}>
            <tr>
              <th style={{ padding: '15px', textAlign: 'left' }}>Titre</th>
              <th style={{ padding: '15px', textAlign: 'left' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {incidents.map(inc => (
              <tr 
                key={inc.id} 
                onClick={() => setSelectedIncident(inc)} // Clic pour sélectionner
                style={{ 
                  borderTop: '1px solid #e5e7eb', 
                  cursor: 'pointer',
                  backgroundColor: selectedIncident?.id === inc.id ? '#eff6ff' : 'transparent' 
                }}
              >
                <td style={{ padding: '15px' }}>
                    <strong>{inc.titre}</strong>
                    <p style={{ margin: '5px 0 0 0', fontSize: '0.9em', color: '#666' }}>{inc.description}</p>
                </td>
                <td style={{ padding: '15px' }}><span style={{ color: '#059669', fontWeight: '500' }}>Ouvert</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Affichage des commentaires si un incident est sélectionné */}
      {selectedIncident && (
        <div style={{ marginTop: '30px', animation: 'fadeIn 0.3s' }}>
            <h3>Détails : {selectedIncident.titre}</h3>
            <CommentList incidentId={selectedIncident.id} />
        </div>
      )}
    </div>
  );
}

export default App;