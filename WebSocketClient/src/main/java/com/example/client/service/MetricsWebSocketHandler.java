package com.example.client.service;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;

public class MetricsWebSocketHandler extends TextWebSocketHandler {

    // Список активных сессий (если нужно)
    private final List<WebSocketSession> activeSessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Вызывается после установления соединения
        activeSessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Обработка полученных метрик
        String metricsData = message.getPayload();
        // Здесь вы можете обработать данные метрик, сохранить их в базу данных или выполнить другие действия
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Вызывается после закрытия соединения
        activeSessions.remove(session);
    }
}


