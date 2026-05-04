package com.projet.notification_service.dto;

import com.projet.notification_service.entity.NotificationChannel;
import com.projet.notification_service.entity.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SendNotificationRequest {

    @NotBlank
    private String recipientKeycloakId;  // Stocké en base (RGPD-safe)

    // ✅ RGPD : l'email n'est utilisé QUE pour l'envoi, jamais persisté en base
    @Email
    private String recipientEmail;

    @NotNull
    private NotificationType type;

    @NotNull
    private NotificationChannel channel;

    @NotBlank
    private String subject;

    @NotBlank
    private String message;

    private Long referenceId;    // Ex: l'ID de l'incident concerné
    private String referenceType; // "INCIDENT" ou "COMMENT"
}