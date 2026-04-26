package com.projet.chat_service.client;

import com.projet.chat_service.dto.IncidentDto;
import com.projet.chat_service.dto.CreateIncidentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "INCIDENT-SERVICE")
public interface IncidentClient {

    // Récupérer tous les incidents avec token explicite
    @GetMapping("/api/incidents")
    List<IncidentDto> getAllIncidentsWithToken(
            @RequestHeader("Authorization") String authorization);

    // Récupérer un incident par ID avec token explicite
    @GetMapping("/api/incidents/{id}")
    IncidentDto getIncidentByIdWithToken(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authorization);

    // Créer un incident avec token explicite
    @PostMapping("/api/incidents")
    IncidentDto createIncidentWithToken(
            @RequestBody CreateIncidentRequest request,
            @RequestHeader("Authorization") String authorization);
}