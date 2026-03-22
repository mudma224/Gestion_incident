import { useState } from "react";
import { Sparkles } from "lucide-react";
import incidentImg from "../../assets/incident.jpg";
import { createIncident } from "../../services/api";

function IncidentForm() {

  const [form, setForm] = useState({
    titre: "",
    description: "",
  });

  const handleSubmit = async (e) => {
  e.preventDefault();

   const payload = {
     titre: form.titre,
     description: form.description,
     statut: "NOUVEAU",
     priorite: "MOYENNE",
   };

   try {
     await createIncident(payload);
     console.log("Incident créé ✅");

    // reset form
    setForm({
      titre: "",
      description: "",
    });

  } catch (error) {
    console.error("Erreur ❌", error);
  }
  };

  return (
    <div className="form-card">
      <img src={incidentImg} className="hero-banner" alt="incident" />
      <div className="hero">
        <div className="hero-text">
           <h1>Signaler un incident</h1>
           <p>Décrivez votre problème et notre équipe ou notre assistant IA vous aidera rapidement.</p>
        </div>
    </div>

      <form onSubmit={handleSubmit}>

        <label className="form-label">Résumé court</label>
        <input placeholder="ex: Impossible de se connecter au VPN" value={form.titre} onChange={(e) => setForm({ ...form, titre: e.target.value })}/>

        <label className="form-label">Description détaillée</label>
        <input type="text" placeholder="Décrivez brièvement votre problème..." value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })}/>

        {/* INFO BOX */}
        <div className="ai-box">
          <div className="ai-icon">
               <Sparkles size={18} />
          </div>

          <div>
            <p className="ai-title">Assistant IA</p>
            <p className="ai-text">
              Gagnez du temps grâce à notre assistant IA, capable de résoudre instantanément les problèmes fréquents (mots de passe, VPN, etc.).
            </p>
          </div>
        </div>

        <div className="actions">
          <button type="button" className="cancel-btn">Annuler</button>
          <button className="primary-btn" disabled={!form.titre || !form.description}>Enregistrer l'incident</button>
        </div>

      </form>

    </div>
  );
}

export default IncidentForm;