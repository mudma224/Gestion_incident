import { BrowserRouter, Routes, Route } from "react-router-dom";
import Sidebar from "./components/layout/Sidebar";
import IncidentForm from "./components/incidents/IncidentForm";
import IncidentsPage from "./pages/IncidentsPage";

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <Sidebar />

        <div className="main">
          <Routes>
            <Route path="/" element={<IncidentsPage />} />
            <Route path="/new" element={<IncidentForm />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;