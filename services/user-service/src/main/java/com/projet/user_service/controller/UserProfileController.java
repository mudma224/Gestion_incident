package com.projet.user_service.controller;

import com.projet.user_service.dto.*;
import com.projet.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    // Appelé à chaque login pour synchroniser le profil Keycloak → BDD
    @PostMapping("/sync")
    public ResponseEntity<UserProfileDto> sync(@RequestBody SyncUserRequest req) {
        return ResponseEntity.ok(service.syncUser(req));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserProfileDto> getByKeycloakId(@PathVariable String keycloakId) {
        return ResponseEntity.ok(service.getByKeycloakId(keycloakId));
    }

    @PostMapping("/{id}/avatar")
    public ResponseEntity<UserProfileDto> uploadAvatar(@PathVariable Long id,
                                                       @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(service.uploadAvatar(id, file));
    }
}