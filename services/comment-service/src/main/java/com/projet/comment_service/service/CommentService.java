package com.projet.comment_service.service;

import com.projet.comment_service.dto.*;
import com.projet.comment_service.entity.Comment;
import com.projet.comment_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final MinioService minioService;

    public CommentDto create(CreateCommentRequest req, String authorKeycloakId) {
        Comment comment = Comment.builder()
                .incidentId(req.getIncidentId())
                .authorKeycloakId(authorKeycloakId)
                .content(req.getContent())
                .attachmentUrls(new ArrayList<>())
                .build();
        return toDto(repository.save(comment));
    }

    public List<CommentDto> getByIncident(Long incidentId) {
        return repository.findByIncidentIdOrderByCreatedAtAsc(incidentId)
                .stream().map(this::toDto).toList();
    }

    public CommentDto addAttachment(Long id, MultipartFile file) throws Exception {
        Comment comment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire introuvable : " + id));
        String url = minioService.uploadFile(file, "comment-" + id);
        comment.getAttachmentUrls().add(url);
        return toDto(repository.save(comment));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private CommentDto toDto(Comment c) {
        return CommentDto.builder()
                .id(c.getId())
                .incidentId(c.getIncidentId())
                .authorKeycloakId(c.getAuthorKeycloakId())
                .content(c.getContent())
                .attachmentUrls(c.getAttachmentUrls())
                .createdAt(c.getCreatedAt())
                .build();
    }
}