package com.projet.chat_service.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class IncidentDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
}
