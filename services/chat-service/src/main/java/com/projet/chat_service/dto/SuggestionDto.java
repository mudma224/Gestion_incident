package com.projet.chat_service.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SuggestionDto {

    private Long id;
    private Long conversationId;
    private Long incidentSimilaireId;
    private Float scoreSimilarite;
    private Boolean accepte;

    // Détails de l'incident suggéré (récupérés depuis incident-service)
    private String titreIncident;
    private String descriptionIncident;
    private String statutIncident;
}