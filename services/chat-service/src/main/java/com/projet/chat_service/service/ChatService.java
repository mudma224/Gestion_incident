package com.projet.chat_service.service;

import com.projet.chat_service.client.IncidentClient;
import com.projet.chat_service.dto.*;
import com.projet.chat_service.entity.*;
import com.projet.chat_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final SuggestionRepository suggestionRepository;
    private final IncidentClient incidentClient;
    private final AIService aiService;
    private final SimpMessagingTemplate messagingTemplate;

    // =========================================================
    // CRÉER UNE NOUVELLE CONVERSATION
    // =========================================================
    public ConversationDto creerConversation(String userKeycloakId) {
        ChatConversation conversation = ChatConversation.builder()
                .sessionId(UUID.randomUUID().toString())
                .userKeycloakId(userKeycloakId)
                .title("Nouvelle conversation")
                .status(ConversationStatus.EN_COURS)
                .build();

        conversation = conversationRepository.save(conversation);
        log.info("Nouvelle conversation creee : {}", conversation.getSessionId());
        return toConversationDto(conversation);
    }

    // =========================================================
    // TRAITER UN MESSAGE — cœur du chatbot
    // =========================================================
    public void traiterMessage(String sessionId,
                               ChatMessageRequest request,
                               String userKeycloakId,
                               String tokenValue) {

        log.info("Traitement du message pour session : {}", sessionId);

        // 1. Récupérer la conversation
        ChatConversation conversation = conversationRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException(
                        "Conversation introuvable : " + sessionId));

        // 2. Sauvegarder le message de l'utilisateur
        ChatMessage messageUser = ChatMessage.builder()
                .conversationId(conversation.getId())
                .role(MessageRole.USER)
                .content(request.getContent())
                .build();
        messageRepository.save(messageUser);
        log.info("Message USER sauvegarde");

        // 3. Envoyer immédiatement le message USER via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + sessionId,
                toMessageResponse(messageUser)
        );

        // 4. Récupérer tous les incidents depuis incident-service
        List<IncidentDto> tousLesIncidents = new ArrayList<>();
        try {
            tousLesIncidents = incidentClient
                    .getAllIncidentsWithToken("Bearer " + tokenValue);
            log.info("{} incident(s) recupere(s) depuis incident-service",
                    tousLesIncidents.size());
        } catch (Exception e) {
            log.error("Impossible de contacter incident-service : {}",
                    e.getMessage());
        }

        // 5. Trouver les incidents similaires
        log.info("Recherche d'incidents similaires pour : {}",
                request.getContent());
        List<IncidentDto> incidentsSimilaires = aiService
                .trouverIncidentsSimilaires(request.getContent(), tousLesIncidents);
        log.info("{} incident(s) similaire(s) trouve(s)",
                incidentsSimilaires.size());

        // 6. Sauvegarder les suggestions
        sauvegarderSuggestions(conversation.getId(),
                incidentsSimilaires,
                request.getContent());

        // 7. Vérifier si l'utilisateur veut créer un incident
        if (Boolean.TRUE.equals(request.getCreerIncident())) {
            log.info("Creation d'un incident demandee");
            creerIncidentDepuisChat(conversation, request, tokenValue);
            return;
        }

        // 8. Appeler l'IA pour générer une réponse
        log.info("Appel Ollama en cours...");
        String reponseIA = aiService.genererReponse(
                request.getContent(),
                incidentsSimilaires
        );
        log.info("Reponse Ollama recue");

        // 9. Extraire les IDs des incidents suggérés
        List<Long> idsIncidentsSuggeres = incidentsSimilaires
                .stream()
                .map(IncidentDto::getId)
                .toList();

        // 10. Sauvegarder la réponse de l'IA
        ChatMessage messageAssistant = ChatMessage.builder()
                .conversationId(conversation.getId())
                .role(MessageRole.ASSISTANT)
                .content(reponseIA)
                .suggestedIncidentIds(idsIncidentsSuggeres.toString())
                .build();
        messageRepository.save(messageAssistant);
        log.info("Reponse ASSISTANT sauvegardee");

        // 11. Envoyer la réponse IA via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + sessionId,
                toMessageResponse(messageAssistant)
        );

        // 12. Mettre à jour le titre si c'est le premier message
        if (messageRepository.countByConversationId(conversation.getId()) <= 2) {
            conversation.setTitle(truncate(request.getContent(), 50));
            conversationRepository.save(conversation);
        }

        log.info("Traitement termine pour session : {}", sessionId);
    }

    // =========================================================
    // SAUVEGARDER LES SUGGESTIONS AVEC SCORE
    // =========================================================
    private void sauvegarderSuggestions(Long conversationId,
                                        List<IncidentDto> incidents,
                                        String messageUtilisateur) {
        log.info("Sauvegarde de {} suggestion(s)", incidents.size());

        // Supprimer les anciennes suggestions
        List<Suggestion> anciennes = suggestionRepository
                .findByConversationId(conversationId);
        if (!anciennes.isEmpty()) {
            suggestionRepository.deleteAll(anciennes);
        }

        // Calculer un score pour chaque incident
        String messageLower = messageUtilisateur.toLowerCase();
        String[] mots = messageLower.split("\\s+");

        for (IncidentDto incident : incidents) {
            float score = calculerScore(incident, mots);

            Suggestion suggestion = Suggestion.builder()
                    .conversationId(conversationId)
                    .incidentSimilaireId(incident.getId())
                    .scoreSimilarite(score)
                    .accepte(false)
                    .build();

            suggestionRepository.save(suggestion);
            log.info("Suggestion sauvegardee : incident #{} score={}",
                    incident.getId(), score);
        }
    }

    // =========================================================
    // CALCUL DU SCORE DE SIMILARITÉ
    // =========================================================
    private float calculerScore(IncidentDto incident, String[] mots) {
        String titre = incident.getTitle() != null ?
                incident.getTitle().toLowerCase() : "";
        String desc  = incident.getDescription() != null ?
                incident.getDescription().toLowerCase() : "";

        int motsTotal = 0;
        int motsCommuns = 0;

        for (String mot : mots) {
            if (mot.length() > 3) {
                motsTotal++;
                if (titre.contains(mot) || desc.contains(mot)) {
                    motsCommuns++;
                }
            }
        }

        if (motsTotal == 0) return 0.0f;
        return (float) motsCommuns / motsTotal;
    }

    // =========================================================
    // CRÉER UN INCIDENT DEPUIS LE CHAT
    // =========================================================
    private void creerIncidentDepuisChat(ChatConversation conversation,
                                         ChatMessageRequest request,
                                         String tokenValue) {
        try {
            CreateIncidentRequest incidentRequest = CreateIncidentRequest.builder()
                    .title(truncate(request.getContent(), 100))
                    .description(request.getContent())
                    .priority("MOYEN")
                    .category("AUTRE")
                    .build();

            IncidentDto incidentCree = incidentClient
                    .createIncidentWithToken(incidentRequest,
                            "Bearer " + tokenValue);
            log.info("Incident #{} cree depuis le chat", incidentCree.getId());

            // Mettre à jour la conversation
            conversation.setStatus(ConversationStatus.INCIDENT_CREE);
            conversation.setIncidentCreatedId(incidentCree.getId());
            conversationRepository.save(conversation);

            // Message de confirmation
            String confirmation = String.format(
                    "Incident cree avec succes !\n\n" +
                            "Incident #%d : %s\n" +
                            "Priorite : MOYEN\n" +
                            "Statut : NOUVEAU\n\n" +
                            "Un technicien vous contactera dans les plus brefs delais.",
                    incidentCree.getId(), incidentCree.getTitle());

            ChatMessage messageConfirmation = ChatMessage.builder()
                    .conversationId(conversation.getId())
                    .role(MessageRole.ASSISTANT)
                    .content(confirmation)
                    .build();
            messageRepository.save(messageConfirmation);

            messagingTemplate.convertAndSend(
                    "/topic/chat/" + conversation.getSessionId(),
                    toMessageResponse(messageConfirmation)
            );

        } catch (Exception e) {
            log.error("Erreur creation incident : {}", e.getMessage());
        }
    }

    // =========================================================
    // MARQUER UNE CONVERSATION COMME RÉSOLUE
    // =========================================================
    public void marquerResolue(String sessionId, Long suggestionId) {
        ChatConversation conversation = conversationRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        if (suggestionId != null) {
            suggestionRepository.findById(suggestionId).ifPresent(s -> {
                s.setAccepte(true);
                suggestionRepository.save(s);
                log.info("Suggestion #{} acceptee", suggestionId);
            });
        }

        conversation.setStatus(ConversationStatus.RESOLU);
        conversationRepository.save(conversation);
        log.info("Conversation {} marquee comme resolue", sessionId);
    }

    // =========================================================
    // RÉCUPÉRER LES SUGGESTIONS D'UNE CONVERSATION
    // =========================================================
    public List<SuggestionDto> getSuggestions(String sessionId, String tokenValue) {
        ChatConversation conversation = conversationRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        return suggestionRepository
                .findByConversationId(conversation.getId())
                .stream()
                .map(s -> {
                    SuggestionDto dto = SuggestionDto.builder()
                            .id(s.getId())
                            .conversationId(s.getConversationId())
                            .incidentSimilaireId(s.getIncidentSimilaireId())
                            .scoreSimilarite(s.getScoreSimilarite())
                            .accepte(s.getAccepte())
                            .build();

                    // Enrichir avec les détails de l'incident
                    try {
                        IncidentDto incident = incidentClient
                                .getIncidentByIdWithToken(
                                        s.getIncidentSimilaireId(),
                                        "Bearer " + tokenValue);
                        dto.setTitreIncident(incident.getTitle());
                        dto.setDescriptionIncident(incident.getDescription());
                        dto.setStatutIncident(incident.getStatus());
                    } catch (Exception e) {
                        log.warn("Impossible de recuperer incident #{}",
                                s.getIncidentSimilaireId());
                    }

                    return dto;
                })
                .toList();
    }

    // =========================================================
    // RÉCUPÉRER L'HISTORIQUE D'UNE CONVERSATION
    // =========================================================
    public List<ChatMessageResponse> getMessages(String sessionId) {
        ChatConversation conversation = conversationRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        return messageRepository
                .findByConversationIdOrderBySentAtAsc(conversation.getId())
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    // =========================================================
    // RÉCUPÉRER TOUTES LES CONVERSATIONS D'UN UTILISATEUR
    // =========================================================
    public List<ConversationDto> getMesConversations(String userKeycloakId) {
        return conversationRepository
                .findByUserKeycloakId(userKeycloakId)
                .stream()
                .map(this::toConversationDto)
                .toList();
    }

    // =========================================================
    // FERMER UNE CONVERSATION
    // =========================================================
    public void fermerConversation(String sessionId) {
        ChatConversation conversation = conversationRepository
                .findBySessionId(sessionId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));
        conversation.setStatus(ConversationStatus.FERMEE);
        conversationRepository.save(conversation);
        log.info("Conversation {} fermee", sessionId);
    }

    // =========================================================
    // UTILITAIRES
    // =========================================================
    private ConversationDto toConversationDto(ChatConversation c) {
        return ConversationDto.builder()
                .id(c.getId())
                .sessionId(c.getSessionId())
                .title(c.getTitle())
                .status(c.getStatus())
                .incidentCreatedId(c.getIncidentCreatedId())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private ChatMessageResponse toMessageResponse(ChatMessage m) {
        List<Long> ids = new ArrayList<>();
        if (m.getSuggestedIncidentIds() != null &&
                !m.getSuggestedIncidentIds().isBlank()) {
            try {
                String clean = m.getSuggestedIncidentIds()
                        .replace("[", "").replace("]", "").trim();
                if (!clean.isEmpty()) {
                    for (String id : clean.split(",")) {
                        ids.add(Long.parseLong(id.trim()));
                    }
                }
            } catch (Exception e) {
                log.warn("Erreur parsing suggestedIncidentIds : {}",
                        e.getMessage());
            }
        }

        return ChatMessageResponse.builder()
                .id(m.getId())
                .role(m.getRole())
                .content(m.getContent())
                .suggestedIncidentIds(ids)
                .sentAt(m.getSentAt())
                .build();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "Conversation";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}