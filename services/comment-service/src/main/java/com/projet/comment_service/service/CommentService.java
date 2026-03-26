package com.projet.comment_service.service;

import com.projet.comment_service.model.Comment;
import com.projet.comment_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository repository;
    public Comment saveComment(Comment c) { return repository.save(c); }
    public List<Comment> getCommentsByIncident(UUID id) { return repository.findByIncidentId(id); }
}