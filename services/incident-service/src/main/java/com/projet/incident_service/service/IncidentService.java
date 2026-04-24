package com.projet.incident_service.service;

import com.projet.incident_service.dto.*;
import com.projet.incident_service.entity.*;
import com.projet.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentService {

    private final IncidentRepository repository;
    private final MinioService minioService;

    public IncidentDto create(CreateIncidentRequest req, String keycloakId) {
        Incident incident = Incident.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority())
                .category(req.getCategory())
                .createdByKeycloakId(keycloakId)
                .status(IncidentStatus.NOUVEAU)
                .build();
        return toDto(repository.save(incident));
    }

    public List<IncidentDto> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public IncidentDto getById(Long id) {
        return toDto(findOrThrow(id));
    }

    public List<IncidentDto> getMyIncidents(String keycloakId) {
        return repository.findByCreatedByKeycloakId(keycloakId)
                .stream().map(this::toDto).toList();
    }

    public List<IncidentDto> getAssignedToMe(String keycloakId) {
        return repository.findByAssignedToKeycloakId(keycloakId)
                .stream().map(this::toDto).toList();
    }

    // Transition de statut — suit le workflow défini
    public IncidentDto updateStatus(Long id, IncidentStatus newStatus) {
        Incident incident = findOrThrow(id);
        validateTransition(incident.getStatus(), newStatus);
        incident.setStatus(newStatus);
        if (newStatus == IncidentStatus.RESOLU) {
            incident.setResolvedAt(LocalDateTime.now());
        }
        return toDto(repository.save(incident));
    }

    public IncidentDto assign(Long id, String technicienKeycloakId) {
        Incident incident = findOrThrow(id);
        incident.setAssignedToKeycloakId(technicienKeycloakId);
        incident.setStatus(IncidentStatus.ASSIGNE);
        return toDto(repository.save(incident));
    }

    public IncidentDto addScreenshot(Long id, MultipartFile file) throws Exception {
        Incident incident = findOrThrow(id);
        String url = minioService.uploadFile(file, incident.getId().toString());
        incident.getScreenshotUrls().add(url);
        return toDto(repository.save(incident));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private void validateTransition(IncidentStatus current, IncidentStatus next) {
        boolean valid = switch (current) {
            case NOUVEAU  -> next == IncidentStatus.ASSIGNE;
            case ASSIGNE  -> next == IncidentStatus.EN_COURS;
            case EN_COURS -> next == IncidentStatus.RESOLU;
            case RESOLU   -> next == IncidentStatus.FERME;
            case FERME    -> false;
        };
        if (!valid) {
            throw new IllegalStateException(
                    "Transition invalide : " + current + " → " + next);
        }
    }

    private Incident findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident introuvable : " + id));
    }

    private IncidentDto toDto(Incident i) {
        return IncidentDto.builder()
                .id(i.getId())
                .title(i.getTitle())
                .description(i.getDescription())
                .status(i.getStatus())
                .priority(i.getPriority())
                .category(i.getCategory())
                .createdByKeycloakId(i.getCreatedByKeycloakId())
                .assignedToKeycloakId(i.getAssignedToKeycloakId())
                .screenshotUrls(i.getScreenshotUrls())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}