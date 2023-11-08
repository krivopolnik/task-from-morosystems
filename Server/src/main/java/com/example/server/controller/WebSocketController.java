package com.example.server.controller;

import com.example.server.model.ChatMessage;
import com.example.server.model.SystemMetrics;
import com.example.server.exceptions.MessageProcessingException;
import com.example.server.service.SystemMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import com.example.server.service.ChatService;

@Controller
public class WebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SystemMetricsService systemMetrics;

    // Method that maps to the '/chat.sendMessage' endpoint and when messages are sent to this endpoint,
    // they are broadcast to the '/topic/public'.
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        try {
            chatService.saveMessage(chatMessage);
            return chatMessage;
        } catch (Exception e) {
            throw new MessageProcessingException("Error saving chat message", e);
        }
    }

    // Method that maps to the '/metrics' endpoint and broadcasts the received system metrics
    // to the '/topic/metrics' for subscribers.
    @MessageMapping("/metrics")
    @SendTo("/topic/metrics")
    public SystemMetrics handleMetrics(@Payload SystemMetrics metrics) {
        try {
            systemMetrics.saveMetrics(metrics);
            return metrics;
        } catch (Exception e) {
            throw new MessageProcessingException("Error saving system metrics", e);
        }
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
