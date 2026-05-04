package com.projet.chat_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageRequest {

    // Le texte du message de l'utilisateur
    // @NotBlank = ne peut pas être null, vide ou juste des espaces
    @NotBlank(message = "Le message ne peut pas être vide")
    private String content;

    // Optionnel : si l'utilisateur veut créer un incident directement
    private Boolean creerIncident;
}