import { LayoutDashboard, AlertTriangle, Bot } from "lucide-react";

function Sidebar() {
  return (
    <div className="sidebar">

      <div>
        <div className="logo">Gestion Incident</div>

        <div className="nav-item active">
          <LayoutDashboard size={18} />
          Signaler un incident
        </div>

        <div className="nav-item">
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