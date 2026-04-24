package com.projet.comment_service.controller;

import com.projet.comment_service.dto.*;
import com.projet.comment_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CreateCommentRequest req,
                                             @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.create(req, jwt.getSubject()));
    }

    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<CommentDto>> getByIncident(@PathVariable Long incidentId) {
        return ResponseEntity.ok(service.getByIncident(incidentId));
    }

    @PostMapping("/{id}/attachments")
    public ResponseEntity<CommentDto> addAttachment(@PathVariable Long id,
                                                    @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.addAttachment(id, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}