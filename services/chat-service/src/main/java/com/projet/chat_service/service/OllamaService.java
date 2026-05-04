package com.projet.chat_service.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    private final RestClient ollamaClient;

    @Value("${ollama.model}")
    private String model;

    public OllamaService(@Qualifier("ollamaClient") RestClient ollamaClient) {
        this.ollamaClient = ollamaClient;
    }

    public String chat(String systemPrompt, String userMessage) {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user",   "content", userMessage)
                ),
                "stream", false
        );

        Map response = ollamaClient.post()
                .uri("/api/chat")
                .body(body)
                .retrieve()
                .body(Map.class);

        // Extrait le texte de la réponse Ollama
        Map message = (Map) response.get("message");
        return (String) message.get("content");
    }

    public boolean isSimilar(String text1, String text2) {
        String prompt = """
            Compare ces deux descriptions de problèmes informatiques.
            Réponds UNIQUEMENT par "OUI" si le problème 2 ressemble au problème 1,
            ou "NON" sinon. Pas d'explication.
            
            Problème 1: %s
            Problème 2: %s
            """.formatted(text1, text2);

        String result = chat("Tu es un assistant de comparaison de tickets.", prompt);
        return result.trim().toUpperCase().contains("OUI");
    }
}