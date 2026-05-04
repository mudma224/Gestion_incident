package com.projet.user_service.controller;

import com.projet.user_service.dto.UserProfileDto;
import com.projet.user_service.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService service;

    @PostMapping("/sync")
    public ResponseEntity<UserProfileDto> sync(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.syncUser(jwt));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.getByKeycloakId(jwt.getSubject()));
    }

    @PreAuthorize("@userAccess.canReadUserById(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PreAuthorize("@userAccess.canReadKeycloakId(#keycloakId, authentication)")
    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserProfileDto> getByKeycloakId(@PathVariable String keycloakId) {
        return ResponseEntity.ok(service.getByKeycloakId(keycloakId));
    }

    @PreAuthorize("@userAccess.canReadUserById(#id, authentication)")
    @PostMapping("/{id}/avatar")
    public ResponseEntity<UserProfileDto> uploadAvatar(@PathVariable Long id,
                                                       @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(service.uploadAvatar(id, file));
    }
}
