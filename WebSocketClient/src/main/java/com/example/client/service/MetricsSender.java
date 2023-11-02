package com.example.client.service;

import com.example.client.model.SystemMetric;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;

@Service
public class MetricsSender  {

    private final String serverUrl = "ws://localhost:8080";
    private WebSocketSession session;

    private final SystemMonitorService systemMonitorService; // Внедрение зависимости

    public MetricsSender(SystemMonitorService systemMonitorService) {
        this.systemMonitorService = systemMonitorService;
    }

    // Метод для установления соединения с сервером
    public void connect() throws Exception {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        this.session = webSocketClient.doHandshake(new MetricsWebSocketHandler(), serverUrl).get();
    }

    // Метод для отправки метрик на сервер
    public void sendMetrics() throws IOException {
        SystemMetric metric = systemMonitorService.gatherSystemInfo();
        // Тут можно преобразовать metric в JSON или другой формат, если требуется
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage("Тут ваше сообщение, возможно в формате JSON"));
        }
    }
}
