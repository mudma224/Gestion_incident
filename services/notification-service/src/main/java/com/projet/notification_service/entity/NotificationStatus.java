package com.projet.notification_service.entity;

public enum NotificationStatus {
    PENDING, // En attente d'envoi
    SENT,    // Envoyée avec succès
    FAILED,  // Échec d'envoi
    READ     // Lue par le destinataire (pour IN_APP)
}