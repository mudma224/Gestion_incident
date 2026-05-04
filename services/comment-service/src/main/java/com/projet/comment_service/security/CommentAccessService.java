package com.projet.comment_service.security;

import com.projet.comment_service.entity.Comment;
import com.projet.comment_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Collection;
import java.util.Map;

@Component("commentAccess")
@RequiredArgsConstructor
public class CommentAccessService {

    @Qualifier("incidentClient")
    private final RestClient incidentClient;
    private final CommentRepository commentRepository;

    public boolean canCreateOnIncident(Long incidentId, Authentication authentication) {
        return canAccessIncident(incidentId, authentication);
    }

    public boolean canReadIncidentComments(Long incidentId, Authentication authentication) {
        return canAccessIncident(incidentId, authentication);
    }

    public boolean canModifyComment(Long commentId, Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        if (jwt == null) {
            return false;
        }

        return commentRepository.findById(commentId)
                .map(comment -> canModifyComment(comment, jwt))
                .orElse(false);
    }

    private boolean canModifyComment(Comment comment, Jwt jwt) {
        if (!canAccessIncident(comment.getIncidentId(), jwt)) {
            return false;
        }

        return hasSupportRole(jwt) || comment.getAuthorKeycloakId().equals(jwt.getSubject());
    }

    private boolean canAccessIncident(Long incidentId, Authentication authentication) {
        Jwt jwt = extractJwt(authentication);
        return jwt != null && canAccessIncident(incidentId, jwt);
    }

    private boolean canAccessIncident(Long incidentId, Jwt jwt) {
        try {
            incidentClient.get()
                    .uri("/api/incidents/{id}", incidentId)
                    .header("Authorization", "Bearer " + jwt.getTokenValue())
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.Forbidden
                 | HttpClientErrorException.Unauthorized
                 | HttpClientErrorException.NotFound ex) {
            return false;
        } catch (RestClientResponseException ex) {
            HttpStatusCode statusCode = ex.getStatusCode();
            return !statusCode.is4xxClientError();
        } catch (Exception ex) {
            return false;
        }
    }

    private Jwt extractJwt(Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        return principal instanceof Jwt jwt ? jwt : null;
    }

    private boolean hasSupportRole(Jwt jwt) {
        Object realmAccess = jwt.getClaims().get("realm_access");
        if (!(realmAccess instanceof Map<?, ?> realmAccessMap)) {
            return false;
        }

        Object roles = realmAccessMap.get("roles");
        if (!(roles instanceof Collection<?> roleValues)) {
            return false;
        }

        return roleValues.stream()
                .map(String::valueOf)
                .anyMatch(role -> "ROLE_ADMIN".equals(role) || "ROLE_TECHNICIEN".equals(role));
    }
}
