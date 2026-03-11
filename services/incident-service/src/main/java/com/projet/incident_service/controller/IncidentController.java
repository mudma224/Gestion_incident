package com.projet.incident_service.controller;

import com.projet.incident_service.model.Incident;
import com.projet.incident_service.service.IncidentService; // On importe le service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173") 

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService; // On injecte le service, plus le repository

    @GetMapping("/test")
    public String test() {
        return "Le Service Incident est opérationnel avec son architecture en couches !";
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