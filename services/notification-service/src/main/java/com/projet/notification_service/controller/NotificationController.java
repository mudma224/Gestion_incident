package com.projet.notification_service.controller;

import com.projet.notification_service.dto.NotificationDto;
import com.projet.notification_service.dto.SendNotificationRequest;
import com.projet.notification_service.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    /**
     * 📤 Endpoint principal — appelé par les autres microservices.
     * Ex: incident-service appelle ici après chaque changement de statut.
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationDto> send(
            @Valid @RequestBody SendNotificationRequest req) {
        return ResponseEntity.ok(service.send(req));
    }

    /**
     * 📥 Mes notifications (utilisateur connecté).
     * ✅ RGPD — Droit d'accès : un utilisateur ne voit QUE ses propres notifications.
     */
    @GetMapping("/me")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.getMyNotifications(jwt.getSubject()));
    }

    /**
     * 🔴 Notifications non lues (pour badge dans l'UI React).
     */
    @GetMapping("/me/unread")
    public ResponseEntity<List<NotificationDto>> getUnread(
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.getUnread(jwt.getSubject()));
    }

    /**
     * 🔢 Compte les non lues (pour afficher "5" sur une cloche dans le header).
     */
    @GetMapping("/me/unread/count")
    public ResponseEntity<Map<String, Long>> countUnread(
            @AuthenticationPrincipal Jwt jwt) {
        long count = service.countUnread(jwt.getSubject());
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * ✅ Marquer une notification comme lue.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.markAsRead(id, jwt.getSubject()));
    }

    /**
     * 🗑️ RGPD — Droit à l'effacement : supprime toutes mes notifications.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAllMyNotifications(
            @AuthenticationPrincipal Jwt jwt) {
        service.deleteAllMyNotifications(jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}