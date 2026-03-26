import { LayoutDashboard, AlertTriangle, Bot } from "lucide-react";
import { useNavigate } from "react-router-dom";

function Sidebar() {
  const navigate = useNavigate(); // ✅ MUST be here

  return (
    <div className="sidebar">

      <div>
        <div className="logo">Gestion Incident</div>

        <div className="nav-item" onClick={() => navigate("/new")}>
          <LayoutDashboard size={18} />
          Signaler un incident
        </div>

        <div className="nav-item" onClick={() => navigate("/")}>
          <AlertTriangle size={18} />
          Mes incidents
        </div>

        <div className="nav-item">
          <Bot size={18} />
          Support IA
        </div>
      </div>

    </div>
  );
}

export default Sidebar;