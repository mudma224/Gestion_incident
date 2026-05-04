package com.projet.chat_service.controller;

import com.projet.chat_service.dto.ChatRequest;
import com.projet.chat_service.dto.ChatResponse;
import com.projet.chat_service.entity.ChatMessage;
import com.projet.chat_service.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @PostMapping({"/send", "/message"})
    public ResponseEntity<ChatResponse> send(@Valid @RequestBody ChatRequest request,
                                             @AuthenticationPrincipal Jwt jwt) {
        String rawToken = jwt.getTokenValue();
        boolean privileged = hasSupportRole(jwt);

        return ResponseEntity.ok(
                chatService.processMessage(request.getMessage(), jwt.getSubject(), rawToken, privileged)
        );
    }

    @GetMapping({"/history", "/history/{conversationId}"})
    public ResponseEntity<List<ChatMessage>> history(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable(required = false) Long conversationId) {
        return ResponseEntity.ok(chatService.getHistory(jwt.getSubject()));
    }

    private boolean hasSupportRole(Jwt jwt) {
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realmAccessMap)) {
            return false;
        }

        Object roles = realmAccessMap.get("roles");
        if (!(roles instanceof Collection<?> roleValues)) {
            return false;
        }

        return roleValues.stream()
                .map(String::valueOf)
                .anyMatch(role -> "ROLE_ADMIN".equals(role) || "ROLE_TECHNICIEN".equals(role));
    }
}
