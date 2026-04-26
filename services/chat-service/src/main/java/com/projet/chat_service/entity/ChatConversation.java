package com.projet.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_conversations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant unique de la session (ex: "550e8400-e29b-41d4-a716")
    // Généré par le service, utilisé par WebSocket
    @Column(nullable = false, unique = true)
    private String sessionId;

    // ID Keycloak de l'utilisateur — comme dans incident-service
    @Column(nullable = false)
    private String userKeycloakId;

    // Titre auto-généré par l'IA (résumé de la conversation)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    // Si l'utilisateur a créé un incident depuis le chat
    // null = pas d'incident créé
    private Long incidentCreatedId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = ConversationStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}