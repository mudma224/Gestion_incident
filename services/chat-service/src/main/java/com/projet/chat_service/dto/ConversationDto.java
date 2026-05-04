package com.projet.chat_service.dto;

import com.projet.chat_service.entity.ConversationStatus;  // ← cet import est-il là ?
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConversationDto {

    private Long id;
    private String sessionId;
    private String title;
    private ConversationStatus status;
    private Long incidentCreatedId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}