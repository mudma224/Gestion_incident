package com.projet.chat_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean("ollamaClient")
    public RestClient ollamaClient(@Value("${ollama.base-url}") String ollamaUrl) {
        return RestClient.builder()
                .baseUrl(ollamaUrl)
                .build();
    }

    @Bean("incidentClient")
    public RestClient incidentClient(@Value("${incident-service.url}") String incidentUrl) {
        return RestClient.builder()
                .baseUrl(incidentUrl)
                .build();
    }
}
