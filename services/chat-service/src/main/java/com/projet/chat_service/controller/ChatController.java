package com.projet.chat_service.controller;

import com.projet.chat_service.client.IncidentClient;
import com.projet.chat_service.dto.*;
import com.projet.chat_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final IncidentClient incidentClient;

    // Créer une nouvelle session de chat
    @PostMapping("/sessions")
    public ResponseEntity<ConversationDto> creerSession(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                chatService.creerConversation(jwt.getSubject()));
    }

    // Récupérer toutes mes conversations
    @GetMapping("/sessions/mes-sessions")
    public ResponseEntity<List<ConversationDto>> getMesSessions(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                chatService.getMesConversations(jwt.getSubject()));
    }

    // Récupérer l'historique des messages
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.getMessages(sessionId));
    }

    // Envoyer un message — retourne immédiatement
    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<Map<String, String>> envoyerMessage(
            @PathVariable String sessionId,
            @RequestBody ChatMessageRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();
        String tokenValue = jwt.getTokenValue();

        new Thread(() ->
                chatService.traiterMessage(sessionId, request, keycloakId, tokenValue)
        ).start();

        return ResponseEntity.ok(Map.of(
                "status", "Message recu - reponse en cours de generation",
                "sessionId", sessionId
        ));
    }

    // Récupérer les suggestions d'une conversation — avec token
    @GetMapping("/sessions/{sessionId}/suggestions")
    public ResponseEntity<List<SuggestionDto>> getSuggestions(
            @PathVariable String sessionId,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                chatService.getSuggestions(sessionId, jwt.getTokenValue()));
    }

    // Marquer une conversation comme résolue
    @PatchMapping("/sessions/{sessionId}/resoudre")
    public ResponseEntity<Map<String, String>> marquerResolue(
            @PathVariable String sessionId,
            @RequestParam(required = false) Long suggestionId) {
        chatService.marquerResolue(sessionId, suggestionId);
        return ResponseEntity.ok(Map.of(
                "status", "Conversation marquee comme resolue",
                "sessionId", sessionId
        ));
    }

    // Fermer une conversation
    @PatchMapping("/sessions/{sessionId}/fermer")
    public ResponseEntity<Void> fermerSession(
            @PathVariable String sessionId) {
        chatService.fermerConversation(sessionId);
        return ResponseEntity.noContent().build();
    }

    // Test temporaire — vérifier Feign
    @GetMapping("/test-incidents")
    public ResponseEntity<List<IncidentDto>> testIncidents(
            @AuthenticationPrincipal Jwt jwt) {
        try {
            List<IncidentDto> incidents = incidentClient
                    .getAllIncidentsWithToken("Bearer " + jwt.getTokenValue());
            log.info("Incidents recuperes via Feign : {}", incidents.size());
            return ResponseEntity.ok(incidents);
        } catch (Exception e) {
            log.error("Erreur Feign : {}", e.getMessage());
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}