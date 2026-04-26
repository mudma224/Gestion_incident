package com.projet.chat_service.dto;

import com.projet.chat_service.entity.MessageRole;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageResponse {

    private Long id;
    private MessageRole role;       // USER ou ASSISTANT
    private String content;         // le texte du message
    private List<Long> suggestedIncidentIds;  // incidents suggérés par l'IA
    private LocalDateTime sentAt;
}