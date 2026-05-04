package com.projet.incident_service.security;

import com.projet.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("incidentAccess")
@RequiredArgsConstructor
public class IncidentAccessService {

    private final IncidentRepository repository;

    public boolean canRead(Long id, Authentication authentication) {
        if (hasAnyRole(authentication, "ROLE_ADMIN", "ROLE_TECHNICIEN")) {
            return true;
        }

        String currentUserId = extractSubject(authentication);
        return repository.findById(id)
                .map(incident -> currentUserId.equals(incident.getCreatedByKeycloakId())
                        || currentUserId.equals(incident.getAssignedToKeycloakId()))
                .orElse(false);
    }

    public boolean canChangeStatus(Long id, Authentication authentication) {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        String currentUserId = extractSubject(authentication);
        return repository.findById(id)
                .map(incident -> currentUserId.equals(incident.getAssignedToKeycloakId()))
                .orElse(false);
    }

    public boolean canUploadScreenshot(Long id, Authentication authentication) {
        return canRead(id, authentication);
    }

    private boolean hasAnyRole(Authentication authentication, String... roles) {
        for (String role : roles) {
            if (hasRole(authentication, role)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private String extractSubject(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getToken().getSubject();
        }
        return authentication.getName();
    }
}
