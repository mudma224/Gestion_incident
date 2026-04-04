package com.projet.incident_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String statut;           // Nouveau, Assigné, En cours, Résolu, Fermé

    @Column(nullable = false)
    private String priorite;         // Basse, Moyenne, Haute

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @Column(name = "utilisateur_id")   // qui a créé l'incident
    private UUID utilisateurId;

    @Column(name = "technicien_id")    // qui est assigné
    private UUID technicienId;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        if (this.statut == null) this.statut = "NOUVEAU";
        if (this.priorite == null) this.priorite = "MOYENNE";
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }
}