package com.projet.chat_service.service;

import com.projet.chat_service.dto.IncidentDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AIService {

    private final RestTemplate restTemplate;

    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${ollama.model}")
    private String model;

    public AIService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(600_000);
        this.restTemplate = new RestTemplate(factory);
    }

    // =========================================================
    // GÉNÉRER UNE RÉPONSE VIA OLLAMA
    // =========================================================
    public String genererReponse(String messageUtilisateur,
                                 List<IncidentDto> incidentsSimilaires) {
        String systemPrompt = buildSystemPrompt(incidentsSimilaires);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system",  "content", systemPrompt),
                        Map.of("role", "user",    "content", messageUtilisateur)
                ),
                "stream", false
        );

        log.info("Appel Ollama avec le modele : {}", model);
        try {
            Map response = restTemplate.postForObject(
                    ollamaBaseUrl + "/api/chat",
                    requestBody,
                    Map.class
            );

            if (response != null) {
                Map message = (Map) response.get("message");
                return (String) message.get("content");
            }
            return "Aucune reponse recue.";

        } catch (Exception e) {
            log.error("Erreur lors de l'appel Ollama : {}", e.getMessage());
            return "Desole, je rencontre un probleme technique. Veuillez reessayer.";
        }
    }

    // =========================================================
    // CONSTRUIRE LE PROMPT SYSTÈME
    // =========================================================
    private String buildSystemPrompt(List<IncidentDto> incidents) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("""
            Tu es un assistant intelligent de support technique.
            Ton role est d'aider les utilisateurs a resoudre leurs problemes informatiques.
            
            Regles importantes :
            - Reponds TOUJOURS en francais
            - Sois concis, clair et precis
            - Propose des solutions etape par etape
            - Si tu n'es pas sur, dis-le honnetement
            - Ne jamais inventer des informations
            """);

        if (!incidents.isEmpty()) {
            prompt.append("\nVoici des incidents similaires deja traites :\n");
            for (IncidentDto incident : incidents) {
                prompt.append(String.format("""
                    ---
                    Incident #%d : %s
                    Categorie : %s | Priorite : %s | Statut : %s
                    Description : %s
                    ---
                    """,
                        incident.getId(),
                        incident.getTitle(),
                        incident.getCategory(),
                        incident.getPriority(),
                        incident.getStatus(),
                        incident.getDescription()
                ));
            }
            prompt.append("\nBase-toi sur ces incidents pour proposer une solution.\n");
        } else {
            prompt.append("""
                \nAucun incident similaire n'a ete trouve dans notre base.
                Propose une solution generale basee sur tes connaissances.
                """);
        }

        return prompt.toString();
    }

    // =========================================================
    // RECHERCHE D'INCIDENTS SIMILAIRES
    // =========================================================
    public List<IncidentDto> trouverIncidentsSimilaires(
            String messageUtilisateur,
            List<IncidentDto> tousLesIncidents) {

        String messageLower = normaliser(messageUtilisateur);
        String[] mots = messageLower.split("\\s+");

        log.info("Mots recherches : {}", Arrays.toString(mots));

        return tousLesIncidents.stream()
                .filter(incident -> {
                    String titre = normaliser(incident.getTitle());
                    String desc  = normaliser(incident.getDescription());

                    log.info("Comparaison avec incident #{} titre='{}'",
                            incident.getId(), titre);

                    for (String mot : mots) {
                        if (mot.length() > 3 &&
                                (titre.contains(mot) || desc.contains(mot))) {
                            log.info("Match trouve : mot='{}' dans incident #{}",
                                    mot, incident.getId());
                            return true;
                        }
                    }
                    return false;
                })
                .limit(5)
                .toList();
    }

    // =========================================================
    // NORMALISER LE TEXTE — minuscules + suppression des accents
    // =========================================================
    private String normaliser(String texte) {
        if (texte == null) return "";
        String lower = texte.toLowerCase();
        // Décompose les caractères accentués puis supprime les accents
        return Normalizer
                .normalize(lower, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}