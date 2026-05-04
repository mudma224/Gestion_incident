package com.projet.chat_service.service;

import com.projet.chat_service.dto.IncidentDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
public class IncidentSearchService {

    private final RestClient incidentClient;
    private final OllamaService ollamaService;

    public IncidentSearchService(@Qualifier("incidentClient") RestClient incidentClient,
                                 OllamaService ollamaService) {
        this.incidentClient = incidentClient;
        this.ollamaService = ollamaService;
    }

    public Optional<IncidentDto> findSimilarResolved(String userMessage,
                                                     String bearerToken,
                                                     boolean privileged) {
        try {
            String incidentsPath = privileged ? "/api/incidents" : "/api/incidents/mes-incidents";
            List<IncidentDto> incidents = incidentClient.get()
                    .uri(incidentsPath)
                    .header("Authorization", "Bearer " + bearerToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (incidents == null) {
                return Optional.empty();
            }

            return incidents.stream()
                    .filter(i -> "RESOLU".equals(i.getStatus()) || "FERME".equals(i.getStatus()))
                    .filter(i -> ollamaService.isSimilar(
                            userMessage,
                            i.getTitle() + " : " + i.getDescription()))
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public IncidentDto createIncident(String title, String description, String bearerToken) {
        var body = new java.util.HashMap<String, String>();
        body.put("title", title);
        body.put("description", description);
        body.put("priority", "MOYEN");
        body.put("category", "AUTRE");

        return incidentClient.post()
                .uri("/api/incidents")
                .header("Authorization", "Bearer " + bearerToken)
                .body(body)
                .retrieve()
                .body(IncidentDto.class);
    }
}
