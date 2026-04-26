package com.projet.chat_service.controller;

import com.projet.chat_service.dto.ChatMessageRequest;
import com.projet.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat/{sessionId}/envoyer")
    public void envoyerMessage(
            @DestinationVariable String sessionId,
            @Payload ChatMessageRequest request,
            Principal principal) {

        log.info("Message WebSocket recu sur session {}", sessionId);

        // Pour WebSocket on n'a pas de JWT facilement accessible
        // On utilise une chaine vide — le token sera null
        // Le frontend doit utiliser REST pour les appels avec token
        new Thread(() ->
                chatService.traiterMessage(sessionId, request, principal.getName(), "")
        ).start();
    }
}