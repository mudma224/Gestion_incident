package com.projet.chat_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_messages",
        indexes = {
                @Index(name = "idx_chat_messages_user_created_at", columnList = "userKeycloakId, createdAt"),
                @Index(name = "idx_chat_messages_related_incident", columnList = "relatedIncidentId"),
                @Index(name = "idx_chat_messages_created_incident", columnList = "createdIncidentId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userKeycloakId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String userMessage;

    @Column(columnDefinition = "TEXT")
    private String botResponse;

    private Long relatedIncidentId;
    private Long createdIncidentId;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
