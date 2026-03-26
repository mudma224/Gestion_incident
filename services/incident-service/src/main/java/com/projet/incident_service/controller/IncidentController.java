package com.projet.incident_service.controller;

import com.projet.incident_service.model.Incident;
import com.projet.incident_service.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; // L'importation CrossOrigin est incluse ici

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") // <-- AJOUTE CETTE LIGNE
@RestController
@RequestMapping("/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping("/test")
    public String test() {
        return "Le Service Incident est opérationnel !";
    }

    @GetMapping
    public List<Incident> getAll() {
        return incidentService.recupererTousLesIncidents();
    }

    @PostMapping
    public Incident create(@RequestBody Incident incident) {
        return incidentService.enregistrerUnIncident(incident);
    }
}