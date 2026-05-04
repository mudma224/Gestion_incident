package com.projet.user_service.security;

import com.projet.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component("userAccess")
@RequiredArgsConstructor
public class UserAccessService {

    private final UserProfileRepository repository;

    public boolean canReadUserById(Long id, Authentication authentication) {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        String currentUserId = extractSubject(authentication);
        return repository.findById(id)
                .map(profile -> profile.getKeycloakId().equals(currentUserId))
                .orElse(false);
    }

    public boolean canReadKeycloakId(String keycloakId, Authentication authentication) {
        return hasRole(authentication, "ROLE_ADMIN")
                || keycloakId.equals(extractSubject(authentication));
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
