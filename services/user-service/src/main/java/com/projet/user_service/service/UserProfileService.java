package com.projet.user_service.service;

import com.projet.user_service.dto.*;
import com.projet.user_service.entity.UserProfile;
import com.projet.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;
    private final MinioService minioService;

    public UserProfileDto syncUser(SyncUserRequest req) {
        UserProfile profile = repository.findByKeycloakId(req.getKeycloakId())
                .orElse(UserProfile.builder()
                        .keycloakId(req.getKeycloakId())
                        .build());

        profile.setUsername(req.getUsername());
        profile.setEmail(req.getEmail());
        profile.setFirstName(req.getFirstName());
        profile.setLastName(req.getLastName());
        profile.setRole(req.getRole());

        return toDto(repository.save(profile));
    }

    public List<UserProfileDto> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public UserProfileDto getById(Long id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id)));
    }

    public UserProfileDto getByKeycloakId(String keycloakId) {
        return toDto(repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + keycloakId)));
    }

    public UserProfileDto uploadAvatar(Long id, MultipartFile file) throws Exception {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable : " + id));
        String url = minioService.uploadAvatar(file, profile.getKeycloakId());
        profile.setAvatarUrl(url);
        return toDto(repository.save(profile));
    }

    private UserProfileDto toDto(UserProfile p) {
        return UserProfileDto.builder()
                .id(p.getId())
                .keycloakId(p.getKeycloakId())
                .username(p.getUsername())
                .email(p.getEmail())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .role(p.getRole())
                .avatarUrl(p.getAvatarUrl())
                .createdAt(p.getCreatedAt())
                .build();
    }
}