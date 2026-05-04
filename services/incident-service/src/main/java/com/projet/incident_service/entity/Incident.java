package com.projet.incident_service.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "incidents",
        indexes = {
                @Index(name = "idx_incidents_created_by", columnList = "createdByKeycloakId"),
                @Index(name = "idx_incidents_assigned_to", columnList = "assignedToKeycloakId"),
                @Index(name = "idx_incidents_status", columnList = "status"),
                @Index(name = "idx_incidents_priority", columnList = "priority")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String createdByKeycloakId;
    private String assignedToKeycloakId;

    @ElementCollection
    @CollectionTable(name = "incident_screenshots", joinColumns = @JoinColumn(name = "incident_id"))
    @Column(name = "url")
    private List<String> screenshotUrls;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = IncidentStatus.NOUVEAU;
        }
        if (screenshotUrls == null) {
            screenshotUrls = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (screenshotUrls == null) {
            screenshotUrls = new ArrayList<>();
        }
    }
}
