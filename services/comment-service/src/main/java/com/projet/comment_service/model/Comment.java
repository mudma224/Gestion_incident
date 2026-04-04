package com.projet.comment_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;          // ← maintenant UUID (cohérent avec Incident)

    @Column(name = "utilisateur_id")
    private UUID utilisateurId;       // ← on ajoute pour la future sécurité Keycloak

    @Column(name = "date_creation", updatable = false, nullable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }
}