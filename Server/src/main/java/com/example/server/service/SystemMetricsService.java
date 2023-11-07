package com.example.server.service;

import com.example.server.model.SystemMetrics;
import com.example.server.repository.SystemMetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemMetricsService {

    private final SystemMetricsRepository systemMetricsRepository;

    @Autowired
    public SystemMetricsService(SystemMetricsRepository systemMetricsRepository) {
        this.systemMetricsRepository = systemMetricsRepository;
    }

    public SystemMetrics saveMetrics(SystemMetrics metrics) {
        return systemMetricsRepository.save(metrics);
    }
}
