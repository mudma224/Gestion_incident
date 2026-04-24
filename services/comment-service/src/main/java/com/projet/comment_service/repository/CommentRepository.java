package com.projet.comment_service.repository;

import com.projet.comment_service.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByIncidentIdOrderByCreatedAtAsc(Long incidentId);
    List<Comment> findByAuthorKeycloakId(String keycloakId);
}
