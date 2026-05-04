package com.projet.notification_service.repository;

import com.projet.notification_service.entity.Notification;
import com.projet.notification_service.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientKeycloakIdOrderByCreatedAtDesc(String keycloakId);

    List<Notification> findByRecipientKeycloakIdAndReadFalseOrderByCreatedAtDesc(String keycloakId);

    List<Notification> findByStatus(NotificationStatus status);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipientKeycloakId = :keycloakId")
    void deleteAllByRecipientKeycloakId(String keycloakId);

    long countByRecipientKeycloakIdAndReadFalse(String keycloakId);
}
