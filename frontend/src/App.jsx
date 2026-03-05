import React, { useState, useEffect } from 'react';
import { getIncidents, createIncident } from './api';
import { AlertCircle, Plus, Send, X } from 'lucide-react';

function App() {
  const [incidents, setIncidents] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [newIncident, setNewIncident] = useState({ titre: '', description: '' });

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
      setNewIncident({ titre: '', description: '' }); // Reset
      setShowForm(false); // Fermer le formulaire
      fetchData(); // Rafraîchir la liste
    } catch (error) {
      alert("Erreur lors de la création de l'incident");
    }
  };

  return (
    <div style={{ padding: '30px', maxWidth: '900px', margin: '0 auto', fontFamily: 'system-ui' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '30px' }}>
        <h1><AlertCircle color="#e11d48" /> MVP Incidents</h1>
        <button 
          onClick={() => setShowForm(!showForm)}
          style={{ backgroundColor: '#2563eb', color: 'white', border: 'none', padding: '3px 5px', borderRadius: '6px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '8px' }}
        >
          {showForm ? <X size={10} /> : <Plus size={10} />}
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

      {/* Tableau identique à l'étape précédente */}
      <div style={{ backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)', overflow: 'hidden' }}>
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead style={{ backgroundColor: '#f3f4f6' }}>
            <tr>
              <th style={{ padding: '15px', textAlign: 'left' }}>Titre</th>
              <th style={{ padding: '15px', textAlign: 'left' }}>Description</th>
              <th style={{ padding: '15px', textAlign: 'left' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {incidents.map(inc => (
              <tr key={inc.id} style={{ borderTop: '1px solid #e5e7eb' }}>
                <td style={{ padding: '15px' }}>{inc.titre}</td>
                <td style={{ padding: '15px' }}>{inc.description}</td>
                <td style={{ padding: '15px' }}><span style={{ color: '#059669', fontWeight: '500' }}>Ouvert</span></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default App;