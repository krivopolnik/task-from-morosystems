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
        // Регистрация эндпоинта, который клиенты будут использовать для подключения к WebSocket
        registry.addEndpoint("/ws").setAllowedOrigins("https://localhost:8443");
        registry.addEndpoint("/ws").setAllowedOrigins("https://localhost:8443").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Настройка простого брокера сообщений, который будет отправлять сообщения на клиентские подписки на указанные префиксы
        registry.enableSimpleBroker("/topic");
        // Настройка префикса для фильтрации адресов назначения, которые будут обрабатываться контроллерами сообщений
        registry.setApplicationDestinationPrefixes("/app");
    }
}
