package com.projet.user_service.dto;

import com.projet.user_service.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserProfileDto {
    private Long id;
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private String avatarUrl;
    private LocalDateTime createdAt;
}