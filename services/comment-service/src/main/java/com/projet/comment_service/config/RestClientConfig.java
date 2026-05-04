package com.projet.comment_service.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    @Qualifier("incidentClient")
    public RestClient incidentClient(@Value("${incident-service.url}") String incidentServiceUrl) {
        return RestClient.builder()
                .baseUrl(incidentServiceUrl)
                .build();
    }
}
