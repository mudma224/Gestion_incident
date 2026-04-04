package com.projet.comment_service.service;

import com.projet.comment_service.model.Comment;
import com.projet.comment_service.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment ajouterCommentaire(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> recupererParIncident(UUID incidentId) {   // ← UUID ici
        return commentRepository.findByIncidentId(incidentId);
    }
}