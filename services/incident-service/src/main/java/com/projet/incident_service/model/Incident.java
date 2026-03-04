package com.projet.incident_service.model;

import jakarta.persistence.*; // Pour les outils de base de données
import lombok.*;            // Pour nous éviter d'écrire les getters/setters
import java.util.UUID;      // Pour générer des identifiants uniques et sécurisés

@Entity // Dit à Java : "Cette classe correspond à une table dans la base de données"
@Getter @Setter // Lombok génère automatiquement les fonctions pour lire/écrire les données
@NoArgsConstructor @AllArgsConstructor // Génère les constructeurs (nécessaires pour Java)
public class Incident {

    @Id // Dit que c'est la clé primaire
    @GeneratedValue(strategy = GenerationType.UUID) // Génère automatiquement un ID complexe (ex: a1-b2...)
    private UUID id;

    private String titre;
    private String description;
    private String statut;   // Nouveau, En cours, Résolu
    private String priorite; // Basse, Moyenne, Haute
}