function IncidentTable({ incidents }) {

  const getStatusClass = (status) => {
    switch (status) {
      case "NOUVEAU":
        return "status-new";
      case "EN_COURS":
        return "status-progress";
      case "RESOLU":
        return "status-done";
      case "FERME":
        return "status-closed";
      default:
        return "status-new";
    }
  };

  if (!incidents || incidents.length === 0) {
    return (
      <div className="empty">
        Aucun incident pour le moment.
      </div>
    );
  }

  return (
    <div className="table-card">

      <table>

        <thead>
          <tr>
            <th>Titre</th>
            <th>Description</th>
            <th>Priorité</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>

          {incidents.map((inc) => (
            <tr key={inc.id}>

              <td>{inc.titre}</td>

              <td>{inc.description}</td>

              <td>
                <span className={`priority ${inc.priorite?.toLowerCase()}`}>
                  {inc.priorite || "MOYENNE"}
                </span>
              </td>

              <td>
                <span className={`status ${getStatusClass(inc.statut)}`}>
                  {inc.statut || "NOUVEAU"}
                </span>
              </td>

            </tr>
          ))}

        </tbody>

      </table>

    </div>
  );
}

export default IncidentTable;