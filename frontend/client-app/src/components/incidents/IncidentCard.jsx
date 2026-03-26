import { AlertCircle, Clock, CheckCircle } from "lucide-react";

function IncidentCard({ incident }) {

  const getIcon = () => {
    switch (incident.statut) {
      case "EN_COURS":
        return <Clock size={18} />;
      case "RESOLU":
        return <CheckCircle size={18} />;
      default:
        return <AlertCircle size={18} />;
    }
  };

  return (
    <div className="card">

      <div style={{ display: "flex", justifyContent: "space-between" }}>

        <div>
          <h3>{incident.titre}</h3>
          <p>{incident.description}</p>
        </div>

        <div>
          {getIcon()}
        </div>

      </div>

      <span className={`status ${incident.statut.toLowerCase()}`}>
        {incident.statut}
      </span>

    </div>
  );
}

export default IncidentCard;