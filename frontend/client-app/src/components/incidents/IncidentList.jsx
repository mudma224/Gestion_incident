import IncidentCard from "./IncidentCard";

function IncidentList({ incidents }) {

  if (!incidents.length) {
    return <p>Aucun incident pour le moment</p>;
  }

  return (
    <div className="list">
      {incidents.map((inc) => (
        <IncidentCard key={inc.id} incident={inc} />
      ))}
    </div>
  );
}

export default IncidentList;