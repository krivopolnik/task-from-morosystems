package com.example.client.service;

import com.example.client.model.SystemMetric;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

@Service
public class SystemMonitorService {

    private long[] prevTicks = null;

    public SystemMetric gatherSystemInfo() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();

        double cpuLoad = 0.0;
        if (prevTicks != null) {
            cpuLoad = hardware.getProcessor().getSystemCpuLoadBetweenTicks(prevTicks);
        }
        prevTicks = hardware.getProcessor().getSystemCpuLoadTicks(); // сохраните текущее состояние для следующего вызова

        long availableMemory = hardware.getMemory().getAvailable();

        return new SystemMetric(cpuLoad, availableMemory); // Это ваш DTO или сущность для отправки данных
    }
}
