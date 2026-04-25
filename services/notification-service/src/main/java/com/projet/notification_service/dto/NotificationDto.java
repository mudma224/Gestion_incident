package com.projet.notification_service.dto;

import com.projet.notification_service.entity.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationDto {
    private Long id;
    private String recipientKeycloakId;
    private NotificationType type;
    private NotificationChannel channel;
    private String subject;
    private String message;
    private Long referenceId;
    private String referenceType;
    private NotificationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
}