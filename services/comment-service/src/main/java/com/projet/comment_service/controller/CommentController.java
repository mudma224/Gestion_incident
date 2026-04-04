package com.projet.comment_service.controller;

import com.projet.comment_service.model.Comment;
import com.projet.comment_service.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public Comment create(@RequestBody Comment comment) {
        return commentService.ajouterCommentaire(comment);
    }

    @GetMapping("/incident/{incidentId}")
    public List<Comment> getByIncident(@PathVariable UUID incidentId) {   // ← UUID ici
        return commentService.recupererParIncident(incidentId);
    }
}