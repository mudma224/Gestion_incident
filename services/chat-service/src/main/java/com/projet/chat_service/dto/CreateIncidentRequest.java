package com.projet.chat_service.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateIncidentRequest {

    private String title;
    private String description;
    private String priority;    // BASSE, MOYENNE, HAUTE, CRITIQUE
    private String category;    // MATERIEL, LOGICIEL, RESEAU, SECURITE, AUTRE
}