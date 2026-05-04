package com.projet.user_service.service;

import com.projet.user_service.dto.UserProfileDto;
import com.projet.user_service.entity.Role;
import com.projet.user_service.entity.UserProfile;
import com.projet.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;
    private final MinioService minioService;

    public UserProfileDto syncUser(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String username = firstNonBlank(jwt.getClaimAsString("preferred_username"), jwt.getClaimAsString("email"), keycloakId);
        String email = firstNonBlank(jwt.getClaimAsString("email"), username + "@placeholder.local");

        UserProfile profile = repository.findByKeycloakId(keycloakId)
                .orElse(UserProfile.builder()
                        .keycloakId(keycloakId)
                        .build());

        ensureUniqueUsername(username, keycloakId);
        ensureUniqueEmail(email, keycloakId);

        profile.setUsername(username);
        profile.setEmail(email);
        profile.setFirstName(jwt.getClaimAsString("given_name"));
        profile.setLastName(jwt.getClaimAsString("family_name"));
        profile.setRole(extractRole(jwt));

        return toDto(repository.save(profile));
    }

    public List<UserProfileDto> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public UserProfileDto getById(Long id) {
        return toDto(repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Utilisateur introuvable : " + id)));
    }

    public UserProfileDto getByKeycloakId(String keycloakId) {
        return toDto(repository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Utilisateur introuvable : " + keycloakId)));
    }

    public UserProfileDto uploadAvatar(Long id, MultipartFile file) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Utilisateur introuvable : " + id));
        String objectKey = minioService.uploadAvatar(file, profile.getKeycloakId());
        profile.setAvatarUrl(objectKey);
        return toDto(repository.save(profile));
    }

    private void ensureUniqueUsername(String username, String currentKeycloakId) {
        repository.findByUsername(username)
                .filter(profile -> !profile.getKeycloakId().equals(currentKeycloakId))
                .ifPresent(profile -> {
                    throw new ResponseStatusException(CONFLICT, "Le nom d'utilisateur est deja utilise.");
                });
    }

    private void ensureUniqueEmail(String email, String currentKeycloakId) {
        repository.findByEmail(email)
                .filter(profile -> !profile.getKeycloakId().equals(currentKeycloakId))
                .ifPresent(profile -> {
                    throw new ResponseStatusException(CONFLICT, "L'adresse email est deja utilisee.");
                });
    }

    private Role extractRole(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map<?, ?> realmAccessMap) {
            Object roles = realmAccessMap.get("roles");
            if (roles instanceof List<?> roleValues) {
                if (roleValues.contains(Role.ROLE_ADMIN.name())) {
                    return Role.ROLE_ADMIN;
                }
                if (roleValues.contains(Role.ROLE_TECHNICIEN.name())) {
                    return Role.ROLE_TECHNICIEN;
                }
            }
        }
        return Role.ROLE_USER;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private UserProfileDto toDto(UserProfile profile) {
        return UserProfileDto.builder()
                .id(profile.getId())
                .keycloakId(profile.getKeycloakId())
                .username(profile.getUsername())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .role(profile.getRole())
                .avatarUrl(minioService.buildAvatarUrl(profile.getAvatarUrl()))
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
