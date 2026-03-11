import { Send } from "lucide-react";

function IncidentForm({ newIncident, setNewIncident, handleSubmit }) {

  return (
    <form className="form-card" onSubmit={handleSubmit}>

      <div className="form-group">
        <label>Titre</label>

        <input
          value={newIncident.titre}
          onChange={(e) =>
            setNewIncident({
              ...newIncident,
              titre: e.target.value,
            })
          }
          placeholder="Ex: Problème réseau"
          required
        />
      </div>

      <div className="form-group">
        <label>Description</label>

        <textarea
          value={newIncident.description}
          onChange={(e) =>
            setNewIncident({
              ...newIncident,
              description: e.target.value,
            })
          }
          placeholder="Décrire l'incident"
          required
        />
      </div>

      <div className="form-group">
        <label>Priorité</label>

        <select
          value={newIncident.priorite}
          onChange={(e) =>
            setNewIncident({
              ...newIncident,
              priorite: e.target.value,
            })
          }
        >
          <option value="BASSE">Basse</option>
          <option value="MOYENNE">Moyenne</option>
          <option value="HAUTE">Haute</option>
        </select>
      </div>

      <button className="success-btn" type="submit">
        <Send size={18} />
        Enregistrer
      </button>

    </form>
  );
}

export default IncidentForm;