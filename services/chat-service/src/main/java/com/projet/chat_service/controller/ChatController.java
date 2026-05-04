package com.projet.chat_service.controller;

import com.projet.chat_service.dto.ChatRequest;
import com.projet.chat_service.dto.ChatResponse;
import com.projet.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void handleMessage(@Payload ChatRequest request,
                              SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new AccessDeniedException("Authentication required");
        }

        String keycloakId = (String) sessionAttributes.get("keycloakId");
        String token = (String) sessionAttributes.get("token");
        boolean privileged = Boolean.TRUE.equals(sessionAttributes.get("privileged"));
        if (keycloakId == null || token == null) {
            throw new AccessDeniedException("Authentication required");
        }

        messagingTemplate.convertAndSend("/topic/chat/" + keycloakId,
                ChatResponse.builder()
                        .message("Analyse de votre probleme en cours...")
                        .botMessage("Analyse de votre probleme en cours...")
                        .type("LOADING")
                        .build());

        ChatResponse response = chatService.processMessage(
                request.getMessage(),
                keycloakId,
                token,
                privileged
        );

        messagingTemplate.convertAndSend("/topic/chat/" + keycloakId, response);
    }
}
