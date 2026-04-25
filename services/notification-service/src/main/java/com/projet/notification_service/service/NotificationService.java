package com.projet.notification_service.service;

import com.projet.notification_service.dto.NotificationDto;
import com.projet.notification_service.dto.SendNotificationRequest;
import com.projet.notification_service.entity.*;
import com.projet.notification_service.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    private final EmailService emailService;

    /**
     * Point d'entrée principal : traite et envoie une notification.
     * Appelé par les autres microservices via POST /api/notifications/send.
     */
    @Transactional
    public NotificationDto send(SendNotificationRequest req) {

        // 1. Persiste la notification en base (état PENDING)
        Notification notification = Notification.builder()
                .recipientKeycloakId(req.getRecipientKeycloakId()) // RGPD-safe
                .type(req.getType())
                .channel(req.getChannel())
                .subject(req.getSubject())
                .message(req.getMessage())
                .referenceId(req.getReferenceId())
                .referenceType(req.getReferenceType())
                .status(NotificationStatus.PENDING)
                .build();

        notification = repository.save(notification);

        // 2. Envoie selon le canal demandé
        boolean emailSent = true;

        if ((req.getChannel() == NotificationChannel.EMAIL
                || req.getChannel() == NotificationChannel.BOTH)
                && req.getRecipientEmail() != null) {
            try {
                emailService.sendHtmlEmail(
                        req.getRecipientEmail(),    // utilisé pour envoi, non persisté
                        req.getSubject(),
                        req.getType().name(),
                        req.getMessage()
                );
            } catch (MessagingException e) {
                emailSent = false;
                log.error("[NOTIF] Échec d'envoi email — type: {} — erreur: {}",
                        req.getType(), e.getMessage());
            }
        }

        // 3. Met à jour le statut selon résultat
        notification.setStatus(emailSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(LocalDateTime.now());
        notification = repository.save(notification);

        log.info("[NOTIF] Notification traitée — type: {} — canal: {} — statut: {}",
                notification.getType(), notification.getChannel(), notification.getStatus());

        return toDto(notification);
    }

    /**
     * Récupère toutes les notifications d'un utilisateur (droit d'accès RGPD).
     */
    public List<NotificationDto> getMyNotifications(String keycloakId) {
        return repository
                .findByRecipientKeycloakIdOrderByCreatedAtDesc(keycloakId)
                .stream().map(this::toDto).toList();
    }

    /**
     * Récupère uniquement les notifications non lues.
     */
    public List<NotificationDto> getUnread(String keycloakId) {
        return repository
                .findByRecipientKeycloakIdAndStatus(keycloakId, NotificationStatus.PENDING)
                .stream().map(this::toDto).toList();
    }

    /**
     * Compte les notifications non lues (pour badge UI).
     */
    public long countUnread(String keycloakId) {
        return repository.countByRecipientKeycloakIdAndStatus(keycloakId, NotificationStatus.PENDING);
    }

    /**
     * Marque une notification comme lue.
     */
    @Transactional
    public NotificationDto markAsRead(Long id, String keycloakId) {
        Notification notif = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Notification introuvable : " + id));

        if (!notif.getRecipientKeycloakId().equals(keycloakId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Accès non autorisé");
        }

        notif.setStatus(NotificationStatus.READ);
        return toDto(repository.save(notif));
    }

    /**
     * ✅ RGPD — Droit à l'effacement : supprime toutes les notifications d'un utilisateur.
     */
    @Transactional
    public void deleteAllMyNotifications(String keycloakId) {
        repository.deleteAllByRecipientKeycloakId(keycloakId);
        log.info("[NOTIF][RGPD] Suppression des notifications pour keycloakId: {}",
                keycloakId.substring(0, 8) + "***"); // masqué dans les logs
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .recipientKeycloakId(n.getRecipientKeycloakId())
                .type(n.getType())
                .channel(n.getChannel())
                .subject(n.getSubject())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .referenceType(n.getReferenceType())
                .status(n.getStatus())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .build();
    }
}