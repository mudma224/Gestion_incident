package com.projet.chat_service.service;

import com.projet.chat_service.dto.ChatResponse;
import com.projet.chat_service.dto.IncidentDto;
import com.projet.chat_service.entity.ChatMessage;
import com.projet.chat_service.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String SYSTEM_PROMPT = """
        Tu es un assistant support technique pour un systeme de gestion des incidents.
        Ton role est d'aider les utilisateurs a resoudre leurs problemes informatiques.
        Reponds en francais, de maniere concise et professionnelle.
        Si tu trouves une solution, explique-la clairement.
        Si tu ne peux pas resoudre le probleme, dis-le honnetement.
        """;

    private final OllamaService ollamaService;
    private final IncidentSearchService searchService;
    private final ChatMessageRepository repository;

    public ChatResponse processMessage(String userMessage,
                                       String keycloakId,
                                       String token,
                                       boolean privileged) {
        Optional<IncidentDto> similar = searchService.findSimilarResolved(userMessage, token, privileged);

        ChatMessage chatMessage = ChatMessage.builder()
                .userKeycloakId(keycloakId)
                .userMessage(userMessage)
                .build();

        ChatResponse response;

        if (similar.isPresent()) {
            IncidentDto incident = similar.get();
            String contextPrompt = SYSTEM_PROMPT + """

                Contexte : Un probleme similaire a deja ete resolu.
                Titre du probleme resolu : %s
                Description : %s
                Utilise ces informations pour aider l'utilisateur.
                """.formatted(incident.getTitle(), incident.getDescription());

            String botReply = ollamaService.chat(contextPrompt, userMessage);
            chatMessage.setBotResponse(botReply);
            chatMessage.setRelatedIncidentId(incident.getId());

            response = ChatResponse.builder()
                    .message(botReply)
                    .botMessage(botReply)
                    .similarIncidentId(incident.getId())
                    .ticketCreated(false)
                    .type("SOLUTION")
                    .build();
        } else {
            String botReply = ollamaService.chat(SYSTEM_PROMPT, userMessage);
            String title = generateIncidentTitle(userMessage);

            IncidentDto newIncident = searchService.createIncident(title, userMessage, token);
            chatMessage.setBotResponse(botReply);
            chatMessage.setCreatedIncidentId(newIncident.getId());

            String finalMessage = botReply + "\n\nUn ticket #" + newIncident.getId()
                    + " a ete cree automatiquement pour votre probleme.";

            response = ChatResponse.builder()
                    .message(finalMessage)
                    .botMessage(finalMessage)
                    .ticketCreated(true)
                    .ticketId(newIncident.getId())
                    .createdIncidentId(newIncident.getId())
                    .type("TICKET_CREATED")
                    .build();
        }

        ChatMessage savedMessage = repository.save(chatMessage);
        response.setConversationId(savedMessage.getId());
        return response;
    }

    public List<ChatMessage> getHistory(String keycloakId) {
        return repository.findByUserKeycloakIdOrderByCreatedAtDesc(keycloakId);
    }

    private String generateIncidentTitle(String userMessage) {
        String generatedTitle = ollamaService.chat(
                "Genere un titre court (max 10 mots) pour ce probleme. Reponds uniquement avec le titre.",
                userMessage
        );

        if (StringUtils.hasText(generatedTitle)) {
            return generatedTitle.trim();
        }

        String normalizedMessage = userMessage == null ? "" : userMessage.trim();
        if (normalizedMessage.isEmpty()) {
            return "Incident signale via chat";
        }

        return normalizedMessage.substring(0, Math.min(normalizedMessage.length(), 80));
    }
}
