import { AlertCircle, Plus, X } from "lucide-react";

function Header({ showForm, setShowForm }) {

  return (
    <header className="header">

      <h1 className="title">
        <AlertCircle size={28} />
        Incident Manager
      </h1>

      <button
        className="primary-btn"
        onClick={() => setShowForm(!showForm)}
      >
        {showForm ? <X size={18} /> : <Plus size={18} />}
        {showForm ? "Annuler" : "Nouvel Incident"}
      </button>

    </header>
  );
}

export default Header;