package com.example.client.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor // Генерирует конструктор с параметрами для всех полей класса
@Getter // Генерирует геттеры для всех полей
@Setter // Генерирует сеттеры для всех полей
public class SystemMetric {
    private double cpuLoad;
    private long availableMemory;
}
