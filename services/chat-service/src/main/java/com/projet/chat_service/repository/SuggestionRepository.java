package com.projet.chat_service.repository;

import com.projet.chat_service.entity.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    // Toutes les suggestions d'une conversation
    List<Suggestion> findByConversationId(Long conversationId);

    // Suggestions acceptées d'une conversation
    List<Suggestion> findByConversationIdAndAccepteTrue(Long conversationId);
}