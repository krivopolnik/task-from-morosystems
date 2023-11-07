package com.example.server.repository;

import com.example.server.model.SystemMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemMetricsRepository extends JpaRepository<SystemMetrics, Long> {
}
