package com.projet.incident_service.service;

import com.projet.incident_service.model.Incident;
import com.projet.incident_service.repository.IncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Dit à Spring : "C'est ici que réside la logique métier"
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    public List<Incident> recupererTousLesIncidents() {
        // C'est ici qu'on pourrait ajouter des filtres plus tard
        return incidentRepository.findAll();
    }

    public Incident enregistrerUnIncident(Incident incident) {
        // C'est ici qu'on ajoutera la logique du Chatbot ou des notifications !
        if (incident.getStatut() == null) {
            incident.setStatut("NOUVEAU"); // Valeur par défaut logicielle
        }
        return incidentRepository.save(incident);
    }
}