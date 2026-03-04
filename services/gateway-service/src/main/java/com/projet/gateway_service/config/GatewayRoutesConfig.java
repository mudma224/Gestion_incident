package com.projet.gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.RequestPredicates;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> incidentRoute() {
        return route("incident_route")
                .route(RequestPredicates.path("/api/incidents/**"), http())
                .filter(stripPrefix(1))                    // Supprime /api
                .filter(lb("INCIDENT-SERVICE"))            // ← C'EST ÇA LA CLÉ (Load Balancer via Eureka)
                .build();
    }
}