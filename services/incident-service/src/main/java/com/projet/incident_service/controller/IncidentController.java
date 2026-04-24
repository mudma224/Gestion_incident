package com.projet.incident_service.controller;

import com.projet.incident_service.dto.*;
import com.projet.incident_service.entity.IncidentStatus;
import com.projet.incident_service.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    @PostMapping
    public ResponseEntity<IncidentDto> create(@Valid @RequestBody CreateIncidentRequest req,
                                              @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.create(req, jwt.getSubject()));
    }

    @GetMapping
    public ResponseEntity<List<IncidentDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/mes-incidents")
    public ResponseEntity<List<IncidentDto>> getMine(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.getMyIncidents(jwt.getSubject()));
    }

    @GetMapping("/assignes")
    public ResponseEntity<List<IncidentDto>> getAssigned(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.getAssignedToMe(jwt.getSubject()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IncidentDto> updateStatus(@PathVariable Long id,
                                                    @RequestParam IncidentStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PatchMapping("/{id}/assigner")
    public ResponseEntity<IncidentDto> assign(@PathVariable Long id,
                                              @RequestParam String technicienId) {
        return ResponseEntity.ok(service.assign(id, technicienId));
    }

    @PostMapping("/{id}/screenshots")
    public ResponseEntity<IncidentDto> addScreenshot(@PathVariable Long id,
                                                     @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.addScreenshot(id, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
