import { Plus, Search } from "lucide-react";
import { useNavigate } from "react-router-dom";

function IncidentsPage() {
  const navigate = useNavigate();

  const incidents = [
    {
      id: 1,
      titre: "Clavier ne fonctionne plus",
      description: "Touches ne répondent plus après café",
      statut: "NOUVEAU",
      priorite: "BASSE",
      date: "22 Mars 2026",
    },
    {
      id: 2,
      titre: "Accès VPN impossible",
      description: "Erreur d’authentification",
      statut: "EN_COURS",
      priorite: "HAUTE",
      date: "20 Mars 2026",
    },
  ];

  return (
    <div className="incidents-page">

      {/* HEADER */}
      <div className="incidents-header">
        <div>
          <h1>Mes incidents</h1>
          <p>Suivez et gérez vos demandes de support IT.</p>
        </div>

        <button
          className="primary-btn"
          onClick={() => navigate("/new")}
        >
          <Plus size={16} />
          Nouvel incident
        </button>
      </div>

      {/* SEARCH */}
      <div className="search-bar">
        <Search size={16} />
        <input placeholder="Rechercher un incident..." />
      </div>

      {/* LIST */}
      <div className="incident-list">
        {incidents.map((inc) => (
          <div key={inc.id} className="incident-card">

            {/* TOP BADGES */}
            <div className="incident-top">
              <span className="badge id">
                INC-{inc.id}
              </span>

              <span className={`badge ${inc.statut.toLowerCase()}`}>
                {inc.statut}
              </span>

              <span className={`badge ${inc.priorite.toLowerCase()}`}>
                {inc.priorite}
              </span>
            </div>

            {/* CONTENT */}
            <h3>{inc.titre}</h3>
            <p>{inc.description}</p>

            {/* FOOTER */}
            <div className="incident-footer">
              <span>{inc.date}</span>
            </div>

          </div>
        ))}
      </div>

    </div>
  );
}

export default IncidentsPage;