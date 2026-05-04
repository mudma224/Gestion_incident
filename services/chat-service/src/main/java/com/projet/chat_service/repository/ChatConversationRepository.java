package com.projet.chat_service.repository;

import com.projet.chat_service.entity.ChatConversation;
import com.projet.chat_service.entity.ConversationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository
        extends JpaRepository<ChatConversation, Long> {

    // Trouver une conversation par son sessionId unique
    Optional<ChatConversation> findBySessionId(String sessionId);

    // Toutes les conversations d'un utilisateur
    List<ChatConversation> findByUserKeycloakId(String userKeycloakId);

    // Conversations actives d'un utilisateur
    List<ChatConversation> findByUserKeycloakIdAndStatus(
            String userKeycloakId,
            ConversationStatus status);
}