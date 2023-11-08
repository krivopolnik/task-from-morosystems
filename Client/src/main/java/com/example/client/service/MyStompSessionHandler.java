package com.example.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.CloseStatus;

@Slf4j
@RequiredArgsConstructor
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private final SystemMonitorService monitorService;

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        // Set the connection status to 'true'
        monitorService.getConnected().set(true);
        // Save the STOMP session for further use
        monitorService.setStompSession(session);
    }

    // Called when a transport error occurs in the STOMP session
    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        monitorService.getConnected().set(false);
        log.error("Transport error: ", exception);
        tryReconnect();
    }

    private void tryReconnect() {
        final int reconnectDelay = 5000;
        try {
            Thread.sleep(reconnectDelay);
            // Initiate reconnection procedure in the monitoring service
            monitorService.scheduleReconnect();
        } catch (InterruptedException ie) {
            // If the current thread was interrupted, restore the interrupted status
            Thread.currentThread().interrupt();
            log.error("Interrupted during the reconnection delay.", ie);
        }
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        log.info("Received a frame. Headers: {}, Payload: {}", headers, payload);
    }

    public void afterConnectionClosed(StompSession session, CloseStatus status) {
        monitorService.getConnected().set(false);
        log.info("Connection closed: {}", status);
        // Initiate reconnection procedure in the monitoring service
        monitorService.scheduleReconnect();
    }
}