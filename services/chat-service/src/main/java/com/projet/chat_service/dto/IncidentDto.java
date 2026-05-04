package com.projet.chat_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IncidentDto {

    private Long id;
    private String title;
    private String description;
    private String status;      // String car on n'a pas l'enum ici
    private String priority;
    private String category;
    private String createdByKeycloakId;
    private LocalDateTime createdAt;
}