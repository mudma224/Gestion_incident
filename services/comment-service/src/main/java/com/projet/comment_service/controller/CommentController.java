package com.projet.comment_service.controller;

import com.projet.comment_service.model.Comment;
import com.projet.comment_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*; // L'importation CrossOrigin est incluse ici
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173") // <-- AJOUTE CETTE LIGNE
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Comment create(@RequestBody Comment comment) {
        return service.saveComment(comment);
    }

    @GetMapping("/incident/{incidentId}")
    public List<Comment> getByIncident(@PathVariable UUID incidentId) {
        return service.getCommentsByIncident(incidentId);
    }
}