package com.projet.comment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateCommentRequest {

    @NotNull
    private Long incidentId;

    @NotBlank
    private String content;
}