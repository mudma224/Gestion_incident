package com.projet.chat_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suggestions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lien vers la conversation
    @Column(nullable = false)
    private Long conversationId;

    // L'incident similaire suggéré
    @Column(nullable = false)
    private Long incidentSimilaireId;

    // Score de similarité entre 0.0 et 1.0
    // ex: 0.85 = 85% similaire
    @Column(nullable = false)
    private Float scoreSimilarite;

    // L'utilisateur a-t-il accepté cette suggestion ?
    // null = pas encore répondu, true = accepté, false = refusé
    private Boolean accepte;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (accepte == null) accepte = false;
    }
}