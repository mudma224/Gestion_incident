import React from 'react';

const config = {
  NOUVEAU:  { label: 'Nouveau',  cls: 'badge-nouveau'  },
  ASSIGNE:  { label: 'Assigné',  cls: 'badge-assigne'  },
  EN_COURS: { label: 'En cours', cls: 'badge-en_cours'  },
  RESOLU:   { label: 'Résolu',   cls: 'badge-resolu'   },
  FERME:    { label: 'Fermé',    cls: 'badge-ferme'    },
  FAIBLE:   { label: 'Faible',   cls: 'badge-faible'   },
  MOYEN:    { label: 'Moyen',    cls: 'badge-moyen'    },
  ELEVE:    { label: 'Élevé',    cls: 'badge-eleve'    },
  CRITIQUE: { label: 'Critique', cls: 'badge-critique' },
};

export default function StatusBadge({ value }) {
  const c = config[value] || { label: value, cls: 'badge-ferme' };
  return (
    <span className={`badge ${c.cls}`}>
      <span className="badge-dot" /> {c.label}
    </span>
  );
}