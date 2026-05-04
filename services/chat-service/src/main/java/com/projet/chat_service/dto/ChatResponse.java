package com.projet.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String message;
    private String botMessage;
    private boolean ticketCreated;
    private Long ticketId;
    private Long createdIncidentId;
    private Long similarIncidentId;
    private Long conversationId;
    private String type;
}
