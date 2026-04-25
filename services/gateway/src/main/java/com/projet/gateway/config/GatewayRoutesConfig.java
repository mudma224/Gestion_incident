package com.projet.gateway.config;

import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> userRoute() {
        return route("user_route")
                .route(RequestPredicates.path("/api/users/**"), http())
                .filter(lb("USER-SERVICE"))   // nom exact dans Eureka
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> incidentRoute() {
        return route("incident_route")
                .route(RequestPredicates.path("/api/incidents/**"), http())
                .filter(lb("INCIDENT-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> commentRoute() {
        return route("comment_route")
                .route(RequestPredicates.path("/api/comments/**"), http())
                .filter(lb("COMMENT-SERVICE"))
                .build();
    }
}