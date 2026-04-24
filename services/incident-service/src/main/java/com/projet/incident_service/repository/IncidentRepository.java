package com.projet.incident_service.repository;

import com.projet.incident_service.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByCreatedByKeycloakId(String keycloakId);
    List<Incident> findByAssignedToKeycloakId(String keycloakId);
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findByPriority(Priority priority);
}
