package com.projet.chat_service.repository;

import com.projet.chat_service.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    // Tous les messages d'une conversation, dans l'ordre chronologique
    List<ChatMessage> findByConversationIdOrderBySentAtAsc(Long conversationId);

    // Compter les messages d'une conversation
    int countByConversationId(Long conversationId);
}