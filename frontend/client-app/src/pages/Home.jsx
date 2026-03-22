import { useState } from "react";
import IncidentForm from "../components/incidents/IncidentForm";
import IncidentList from "../components/incidents/IncidentList";

function Home() {

  const [incidents, setIncidents] = useState([]);

  const addIncident = (data) => {
    setIncidents([
      ...incidents,
      {
        id: Date.now(),
        ...data,
        statut: "NOUVEAU",
      },
    ]);
  };

  return (
    <div className="container">

      <IncidentForm onCreate={addIncident} />

      <IncidentList incidents={incidents} />

    </div>
  );
}

export default Home;