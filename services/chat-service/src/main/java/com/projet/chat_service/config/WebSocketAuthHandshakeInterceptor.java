package com.projet.chat_service.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Map;

@Component
public class WebSocketAuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtDecoder jwtDecoder;

    public WebSocketAuthHandshakeInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = resolveToken(request);
        if (!StringUtils.hasText(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            Jwt jwt = jwtDecoder.decode(token);
            attributes.put("keycloakId", jwt.getSubject());
            attributes.put("token", token);
            attributes.put("privileged", hasSupportRole(jwt));
            return true;
        } catch (JwtException ex) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // No-op
    }

    private String resolveToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        String accessToken = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("access_token");

        return StringUtils.hasText(accessToken) ? accessToken : null;
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
