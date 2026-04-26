import React from 'react';
import { useLocation } from 'react-router-dom';
import { useKeycloak } from '@react-keycloak/web';
import { Bell, LogOut, ChevronRight } from 'lucide-react';

const breadcrumbs = {
  '/dashboard': ['Dashboard'],
  '/incidents': ['Dashboard', 'Incidents'],
  '/users':     ['Dashboard', 'Utilisateurs'],
};

function Navbar() {
  const location = useLocation();
  const { keycloak } = useKeycloak();

  const parts = Object.entries(breadcrumbs).find(([p]) =>
    location.pathname.startsWith(p)
  )?.[1] || ['Dashboard'];

  return (
    <div className="navbar">
      <div className="navbar-breadcrumb">
        {parts.map((part, i) => (
          <React.Fragment key={part}>
            {i > 0 && <ChevronRight size={13} className="navbar-divider" />}
            <span className={i === parts.length - 1 ? 'navbar-breadcrumb-current' : ''}>
              {part}
            </span>
          </React.Fragment>
        ))}
      </div>

      <div className="navbar-actions">
        <button className="icon-btn"><Bell size={15} /></button>
        <button className="logout-btn" onClick={() => keycloak.logout()}>
          <LogOut size={13} />
          Déconnexion
        </button>
      </div>
    </div>
  );
}

export default Navbar;