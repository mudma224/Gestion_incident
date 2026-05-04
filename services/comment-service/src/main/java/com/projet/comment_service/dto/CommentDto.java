package com.projet.comment_service.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentDto {
    private Long id;
    private Long incidentId;
    private String authorKeycloakId;
    private String content;
    private List<String> attachmentUrls;
    private LocalDateTime createdAt;
}
