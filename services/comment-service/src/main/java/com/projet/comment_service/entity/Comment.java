package com.projet.comment_service.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
        name = "comments",
        indexes = {
                @Index(name = "idx_comments_incident_created_at", columnList = "incidentId, createdAt"),
                @Index(name = "idx_comments_author", columnList = "authorKeycloakId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long incidentId;

    @Column(nullable = false)
    private String authorKeycloakId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ElementCollection
    @CollectionTable(name = "comment_attachments", joinColumns = @JoinColumn(name = "comment_id"))
    @Column(name = "url")
    private List<String> attachmentUrls;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (attachmentUrls == null) {
            attachmentUrls = new ArrayList<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (attachmentUrls == null) {
            attachmentUrls = new ArrayList<>();
        }
    }
}
