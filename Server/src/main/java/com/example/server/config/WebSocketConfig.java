package com.example.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register an endpoint that clients will use to connect to the WebSocket
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:8080");
        // Register the same endpoint with SockJS fallback options for browsers that don't support WebSocket
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:8080").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set up a simple message broker to carry messages back to the client on specific topic destinations
        registry.enableSimpleBroker("/topic");
        // Configure a prefix to filter destinations that will be handled by message-handling methods (controllers)
        registry.setApplicationDestinationPrefixes("/app");
    }
}

