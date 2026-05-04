package com.projet.comment_service.service;

import com.projet.comment_service.dto.CommentDto;
import com.projet.comment_service.dto.CreateCommentRequest;
import com.projet.comment_service.entity.Comment;
import com.projet.comment_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
                .stream()
                .map(this::toDto)
                .toList();
    }

    public CommentDto addAttachment(Long id, MultipartFile file) {
        Comment comment = findOrThrow(id);
        if (comment.getAttachmentUrls() == null) {
            comment.setAttachmentUrls(new ArrayList<>());
        }

        String objectKey = minioService.uploadFile(file, "comment-" + id);
        comment.getAttachmentUrls().add(objectKey);
        return toDto(repository.save(comment));
    }

    public void delete(Long id) {
        Comment comment = findOrThrow(id);
        repository.delete(comment);
    }

    private Comment findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Commentaire introuvable : " + id
                ));
    }

    private CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .incidentId(comment.getIncidentId())
                .authorKeycloakId(comment.getAuthorKeycloakId())
                .content(comment.getContent())
                .attachmentUrls(resolveAttachmentUrls(comment.getAttachmentUrls()))
                .createdAt(comment.getCreatedAt())
                .build();
    }

    private List<String> resolveAttachmentUrls(List<String> attachmentReferences) {
        if (attachmentReferences == null || attachmentReferences.isEmpty()) {
            return List.of();
        }

        return attachmentReferences.stream()
                .map(minioService::buildFileUrl)
                .toList();
    }
}
