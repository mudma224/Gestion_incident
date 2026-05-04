package com.projet.notification_service.dto;

import com.projet.notification_service.entity.NotificationChannel;
import com.projet.notification_service.entity.NotificationStatus;
import com.projet.notification_service.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private boolean read;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}
