package com.example.client.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class SystemMetrics implements Serializable {
    private double cpuLoad;
    private long freeMemory;
}
