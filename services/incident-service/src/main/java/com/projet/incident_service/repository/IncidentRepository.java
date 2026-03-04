package com.projet.incident_service.repository;

import com.projet.incident_service.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

// On crée une "Interface". C'est comme un contrat.
// JpaRepository contient déjà toutes les fonctions : save(), findAll(), delete()...
public interface IncidentRepository extends JpaRepository<Incident, UUID> {
}