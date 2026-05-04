package com.projet.chat_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker  // active WebSocket + STOMP dans Spring
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Préfixe des topics auxquels le CLIENT s'abonne pour recevoir
        // ex: le client écoute "/topic/chat/ma-session-123"
        config.enableSimpleBroker("/topic");

        // Préfixe des endpoints auxquels le CLIENT envoie des messages
        // ex: le client envoie vers "/app/chat/envoyer"
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Point de connexion WebSocket initial
        // Le client se connecte d'abord à ws://localhost:8084/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")  // autorise toutes les origines (dev)
                .withSockJS();                  // fallback SockJS si WebSocket indisponible
    }
}