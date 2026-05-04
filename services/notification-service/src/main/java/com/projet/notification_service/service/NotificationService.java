package com.projet.notification_service.service;

import com.projet.notification_service.dto.NotificationDto;
import com.projet.notification_service.dto.SendNotificationRequest;
import com.projet.notification_service.entity.Notification;
import com.projet.notification_service.entity.NotificationChannel;
import com.projet.notification_service.entity.NotificationStatus;
import com.projet.notification_service.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository repository;
    private final EmailService emailService;

    @Transactional
    public NotificationDto send(SendNotificationRequest req) {
        Notification notification = Notification.builder()
                .recipientKeycloakId(req.getRecipientKeycloakId())
                .type(req.getType())
                .channel(req.getChannel())
                .subject(req.getSubject())
                .message(req.getMessage())
                .referenceId(req.getReferenceId())
                .referenceType(req.getReferenceType())
                .status(NotificationStatus.PENDING)
                .build();

        notification = repository.save(notification);

        boolean emailSent = true;
        if ((req.getChannel() == NotificationChannel.EMAIL
                || req.getChannel() == NotificationChannel.BOTH)
                && req.getRecipientEmail() != null) {
            try {
                emailService.sendHtmlEmail(
                        req.getRecipientEmail(),
                        req.getSubject(),
                        req.getType().name(),
                        req.getMessage()
                );
            } catch (MessagingException e) {
                emailSent = false;
                log.error("[NOTIF] Echec d'envoi email - type: {} - erreur: {}",
                        req.getType(), e.getMessage());
            }
        }

        notification.setStatus(emailSent ? NotificationStatus.SENT : NotificationStatus.FAILED);
        notification.setSentAt(LocalDateTime.now());
        notification = repository.save(notification);

        log.info("[NOTIF] Notification traitee - type: {} - canal: {} - statut: {}",
                notification.getType(), notification.getChannel(), notification.getStatus());

        return toDto(notification);
    }

    public List<NotificationDto> getMyNotifications(String keycloakId) {
        return repository.findByRecipientKeycloakIdOrderByCreatedAtDesc(keycloakId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<NotificationDto> getUnread(String keycloakId) {
        return repository.findByRecipientKeycloakIdAndReadFalseOrderByCreatedAtDesc(keycloakId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public long countUnread(String keycloakId) {
        return repository.countByRecipientKeycloakIdAndReadFalse(keycloakId);
    }

    @Transactional
    public NotificationDto markAsRead(Long id, String keycloakId) {
        Notification notif = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification introuvable : " + id));

        if (!notif.getRecipientKeycloakId().equals(keycloakId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acces non autorise");
        }

        notif.setRead(true);
        notif.setReadAt(LocalDateTime.now());
        return toDto(repository.save(notif));
    }

    @Transactional
    public void deleteAllMyNotifications(String keycloakId) {
        repository.deleteAllByRecipientKeycloakId(keycloakId);
        log.info("[NOTIF][RGPD] Suppression des notifications pour keycloakId: {}",
                maskKeycloakId(keycloakId));
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .recipientKeycloakId(notification.getRecipientKeycloakId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .referenceType(notification.getReferenceType())
                .status(notification.getStatus())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .build();
    }

    private String maskKeycloakId(String keycloakId) {
        if (keycloakId == null || keycloakId.isBlank()) {
            return "***";
        }

        return keycloakId.substring(0, Math.min(keycloakId.length(), 8)) + "***";
    }
}
