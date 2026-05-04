package com.projet.incident_service.dto;

import com.projet.incident_service.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateIncidentRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Priority priority;

    private Category category;
}
