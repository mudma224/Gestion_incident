import { useState, useEffect } from "react";
import { getIncidents, createIncident } from "./services/api";

import Header from "./components/Header";
import IncidentForm from "./components/IncidentForm";
import IncidentTable from "./components/IncidentTable";

function App() {

  const [incidents, setIncidents] = useState([]);
  const [showForm, setShowForm] = useState(false);

  const [newIncident, setNewIncident] = useState({
    titre: "",
    description: "",
    priorite: "MOYENNE",
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const response = await getIncidents();
      setIncidents(response.data);
    } catch (error) {
      console.error(error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      await createIncident(newIncident);

      setNewIncident({
        titre: "",
        description: "",
        priorite: "MOYENNE",
      });

      setShowForm(false);
      fetchData();

    } catch (error) {
      console.error(error);
      alert("Erreur lors de la création de l'incident");
    }
  };

  return (
    <div className="container">

      <Header showForm={showForm} setShowForm={setShowForm} />

      {showForm && (
        <IncidentForm
          newIncident={newIncident}
          setNewIncident={setNewIncident}
          handleSubmit={handleSubmit}
        />
      )}

      <IncidentTable incidents={incidents} />

    </div>
  );
}

export default App;