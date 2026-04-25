package com.projet.notification_service.repository;

import com.projet.notification_service.entity.Notification;
import com.projet.notification_service.entity.NotificationStatus;
import com.projet.notification_service.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Toutes les notifications d'un utilisateur (pour "mes notifications")
    List<Notification> findByRecipientKeycloakIdOrderByCreatedAtDesc(String keycloakId);

    // Notifications non lues d'un utilisateur
    List<Notification> findByRecipientKeycloakIdAndStatus(String keycloakId, NotificationStatus status);

    // Pour statistiques / monitoring
    List<Notification> findByStatus(NotificationStatus status);

    // ✅ RGPD — Droit à l'effacement : supprime toutes les notifs d'un utilisateur
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipientKeycloakId = :keycloakId")
    void deleteAllByRecipientKeycloakId(String keycloakId);

    // Compte les non-lues (badge UI)
    long countByRecipientKeycloakIdAndStatus(String keycloakId, NotificationStatus status);
}