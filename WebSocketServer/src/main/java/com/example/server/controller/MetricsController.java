package com.example.server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class MetricsController {

    @MessageMapping("/metrics")
    public void handleMetrics(@Payload String metrics) {
        // Здесь ваш код для обработки метрик.
        // В этом примере метрики представлены в виде строки, но вы можете использовать другие структуры данных.
        System.out.println("Received metrics: " + metrics);
    }
}