package com.projet.user_service.dto;

import com.projet.user_service.entity.Role;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SyncUserRequest {
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}