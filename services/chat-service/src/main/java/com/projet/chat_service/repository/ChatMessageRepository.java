package com.projet.chat_service.repository;

import com.projet.chat_service.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserKeycloakIdOrderByCreatedAtDesc(String keycloakId);
}
