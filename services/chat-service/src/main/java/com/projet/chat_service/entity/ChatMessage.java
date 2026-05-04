package com.projet.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers la conversation parente
    @Column(nullable = false)
    private Long conversationId;

    // USER ou ASSISTANT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    // Le contenu du message (texte libre)
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // IDs des incidents suggérés par l'IA pour ce message
    // Stockés en JSON simple ex: "12,45,67" — null si aucune suggestion
    private String suggestedIncidentIds;

    @Column(updatable = false)
    private LocalDateTime sentAt;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }
}