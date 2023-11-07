package com.example.client.service;

import com.example.client.model.SystemMetrics;
import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.PostConstruct;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

@Service
public class SystemMonitorService {

    private final String url = "ws://localhost:8080/ws"; // URL серверного WebSocket endpoint
    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    @PostConstruct
    public void init() {
        // Инициализация WebSocket клиента и StompClient
        WebSocketClient client = new StandardWebSocketClient();
        this.stompClient = new WebSocketStompClient(client);
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // Установить соединение
        stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                System.err.println("Transport error: " + exception.getMessage());
            }
        });
    }

    @Scheduled(fixedRate = 30000)
    public void reportCurrentMetrics() {
        if (stompSession == null || !stompSession.isConnected()) {
            // Подключение если сессия null или не подключена
            stompClient.connect(url, new StompSessionHandlerAdapter() {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    stompSession = session;
                    sendMetrics();
                }
            });
        } else {
            // Сессия уже подключена, отправить метрики
            sendMetrics();
        }
    }

    private void sendMetrics() {
        if (stompSession != null && stompSession.isConnected()) {
            OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                    OperatingSystemMXBean.class);

            // Сбор метрик
            DecimalFormat cpuFormat = new DecimalFormat("#.0"); // Один знак после запятой для CPU
            double cpuLoad = osBean.getSystemCpuLoad() * 100;
            cpuLoad = Double.valueOf(cpuFormat.format(cpuLoad));

            long freeMemory = osBean.getFreePhysicalMemorySize();
            SystemMetrics metrics = new SystemMetrics(cpuLoad, freeMemory);

            // Отправка метрик
            stompSession.send("/app/metrics", metrics);
        }
    }
}
