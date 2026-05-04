package com.projet.comment_service.controller;

import com.projet.comment_service.dto.CommentDto;
import com.projet.comment_service.dto.CreateCommentRequest;
import com.projet.comment_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PreAuthorize("@commentAccess.canCreateOnIncident(#p0.incidentId, authentication)")
    @PostMapping
    public ResponseEntity<CommentDto> create(@Valid @RequestBody CreateCommentRequest req,
                                             @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.create(req, jwt.getSubject()));
    }

    @PreAuthorize("@commentAccess.canReadIncidentComments(#p0, authentication)")
    @GetMapping("/incident/{incidentId}")
    public ResponseEntity<List<CommentDto>> getByIncident(@PathVariable Long incidentId) {
        return ResponseEntity.ok(service.getByIncident(incidentId));
    }

    @PreAuthorize("@commentAccess.canModifyComment(#p0, authentication)")
    @PostMapping("/{id}/attachments")
    public ResponseEntity<CommentDto> addAttachment(@PathVariable Long id,
                                                    @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.addAttachment(id, file));
    }

    @PreAuthorize("@commentAccess.canModifyComment(#p0, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
