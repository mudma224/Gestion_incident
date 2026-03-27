package com.projet.comment_service.repository;

import com.projet.comment_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Une méthode personnalisée pour récupérer les commentaires d'un incident précis
    List<Comment> findByIncidentId(Long incidentId);
}