package com.projet.incident_service.dto;

import com.projet.incident_service.entity.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IncidentDto {
    private Long id;
    private String title;
    private String description;
    private IncidentStatus status;
    private Priority priority;
    private Category category;
    private String createdByKeycloakId;
    private String assignedToKeycloakId;
    private List<String> screenshotUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}