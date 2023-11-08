package com.example.client.service;

import com.example.client.model.SystemMetrics;
import com.sun.management.OperatingSystemMXBean;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.WebSocketContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
@Service
public class SystemMonitorService {

    private static final int MAX_RETRY_ATTEMPTS = 5;
    private static final long INITIAL_RETRY_INTERVAL = 5000L;

    @Getter
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private WebSocketStompClient stompClient;
    @Getter @Setter
    private StompSession stompSession;
    @Getter @Setter
    private AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicInteger retryCount = new AtomicInteger(0);
    private final String url = "ws://localhost:8081/ws"; // WebSocket URL

    @PostConstruct
    public void init() {
        initializeWebSocketClient();
        connectToStomp();
    }

    // Initializes the WebSocket client with necessary configurations
    private void initializeWebSocketClient() {
        WebSocketClient client = new StandardWebSocketClient(); // Create a standard WebSocket client
        this.stompClient = new WebSocketStompClient(client); // Create a STOMP client using the WebSocket client
        this.stompClient.setMessageConverter(new MappingJackson2MessageConverter()); // Set the message converter for JSON mapping
    }

    // Connects to the STOMP broker using the STOMP client
    private void connectToStomp() {
        stompClient.connect(url, new MyStompSessionHandler(this)); // Initiate the connection
    }

    // Schedules a reconnect attempt in case of connection failure
    public void scheduleReconnect() {
        if (retryCount.incrementAndGet() > MAX_RETRY_ATTEMPTS) { // Check if max retry attempts have been exceeded
            log.error("Reached max number of reconnection attempts");
            return; // If max attempts exceeded, do not attempt to reconnect
        }

        // Calculate the delay for the next retry attempt, exponentially increasing
        long retryInterval = INITIAL_RETRY_INTERVAL * (long) Math.pow(2, retryCount.get() - 1);
        log.info("Scheduling a reconnect in {} ms", retryInterval);
        // Schedule the reconnect attempt after the calculated delay
        scheduler.schedule(this::connectToStomp, retryInterval, TimeUnit.MILLISECONDS);
    }

    // Method to periodically send system metrics
    @Scheduled(fixedRate = 30000)
    public void reportCurrentMetrics() {
        if (stompSession == null || !stompSession.isConnected()) {
            // If not connected, attempt to connect to STOMP
            connectToStomp();
        } else {
            // If already connected, send the current metrics
            sendMetrics();
        }
    }

    // Sends the gathered system metrics to a predefined endpoint
    private void sendMetrics() {
        SystemMetrics metrics = gatherSystemMetrics(); // Gather the current system metrics
        if (stompSession != null && stompSession.isConnected()) {
            // If connected, send the metrics through the STOMP session
            stompSession.send("/app/metrics", metrics);
        }
    }

    // Gathers system metrics such as CPU load and free memory
    private SystemMetrics gatherSystemMetrics() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class); // Get the OS bean
        double cpuLoad = formatCpuLoad(osBean.getCpuLoad() * 100); // Calculate CPU load as a percentage
        long freeMemory = osBean.getFreeMemorySize(); // Get the available free memory
        return new SystemMetrics(cpuLoad, freeMemory); // Return a new SystemMetrics object with the gathered data
    }

    // Formats the CPU load to one decimal place
    private double formatCpuLoad(double cpuLoad) {
        DecimalFormat cpuFormat = new DecimalFormat("#.0"); // Define the decimal format
        return Double.parseDouble(cpuFormat.format(cpuLoad)); // Format and return the CPU load
    }
}