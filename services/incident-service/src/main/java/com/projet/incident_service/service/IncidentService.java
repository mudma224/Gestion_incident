package com.projet.incident_service.service;

import com.projet.incident_service.dto.CreateIncidentRequest;
import com.projet.incident_service.dto.IncidentDto;
import com.projet.incident_service.entity.Incident;
import com.projet.incident_service.entity.IncidentStatus;
import com.projet.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                .screenshotUrls(new ArrayList<>())
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
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<IncidentDto> getAssignedToMe(String keycloakId) {
        return repository.findByAssignedToKeycloakId(keycloakId)
                .stream()
                .map(this::toDto)
                .toList();
    }

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

    public IncidentDto addScreenshot(Long id, MultipartFile file) {
        Incident incident = findOrThrow(id);
        if (incident.getScreenshotUrls() == null) {
            incident.setScreenshotUrls(new ArrayList<>());
        }

        String objectKey = minioService.uploadFile(file, "incident-" + incident.getId());
        incident.getScreenshotUrls().add(objectKey);
        return toDto(repository.save(incident));
    }

    public void delete(Long id) {
        Incident incident = findOrThrow(id);
        repository.delete(incident);
    }

    private void validateTransition(IncidentStatus current, IncidentStatus next) {
        boolean valid = switch (current) {
            case NOUVEAU -> next == IncidentStatus.ASSIGNE;
            case ASSIGNE -> next == IncidentStatus.EN_COURS;
            case EN_COURS -> next == IncidentStatus.RESOLU;
            case RESOLU -> next == IncidentStatus.FERME;
            case FERME -> false;
        };

        if (!valid) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transition invalide : " + current + " -> " + next
            );
        }
    }

    private Incident findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Incident introuvable : " + id
                ));
    }

    private IncidentDto toDto(Incident incident) {
        return IncidentDto.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .status(incident.getStatus())
                .priority(incident.getPriority())
                .category(incident.getCategory())
                .createdByKeycloakId(incident.getCreatedByKeycloakId())
                .assignedToKeycloakId(incident.getAssignedToKeycloakId())
                .screenshotUrls(resolveScreenshotUrls(incident.getScreenshotUrls()))
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }

    private List<String> resolveScreenshotUrls(List<String> screenshotReferences) {
        if (screenshotReferences == null || screenshotReferences.isEmpty()) {
            return List.of();
        }

        return screenshotReferences.stream()
                .map(minioService::buildFileUrl)
                .toList();
    }
}
