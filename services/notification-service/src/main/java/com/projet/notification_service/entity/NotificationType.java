package com.projet.notification_service.entity;

public enum NotificationType {
    INCIDENT_CREATED,        // Un incident vient d'être créé
    INCIDENT_ASSIGNED,       // Un incident t'a été assigné
    INCIDENT_STATUS_CHANGED, // Le statut d'un incident a changé
    COMMENT_ADDED,           // Un commentaire a été ajouté sur ton incident
    INCIDENT_RESOLVED,       // Ton incident a été résolu
    INCIDENT_CLOSED,         // Ton incident a été clôturé
    SYSTEM                   // Notification système générique
}