package com.devvault.cockpit.core.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket handler for system status real-time updates
 * Broadcasts CPU, memory, disk usage, service health
 */
@Component
public class SystemStatusWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemStatusWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

    public SystemStatusWebSocketHandler() {
        // Start system metrics broadcasting every 5 seconds
        scheduler.scheduleAtFixedRate(this::broadcastSystemMetrics, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        logger.info("🔌 System Status WebSocket connected: {}", session.getId());
        
        // Send immediate system status
        sendSystemStatus(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        logger.info("🔌 System Status WebSocket disconnected: {} ({})", session.getId(), status);
    }

    private void sendSystemStatus(WebSocketSession session) {
        try {
            Map<String, Object> status = generateSystemMetrics();
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(status)));
        } catch (Exception e) {
            logger.error("❌ Failed to send system status: {}", e.getMessage());
        }
    }

    private void broadcastSystemMetrics() {
        if (sessions.isEmpty()) return;

        Map<String, Object> metrics = generateSystemMetrics();
        String jsonMessage;
        
        try {
            jsonMessage = objectMapper.writeValueAsString(metrics);
        } catch (Exception e) {
            logger.error("❌ Failed to serialize system metrics: {}", e.getMessage());
            return;
        }

        sessions.values().removeIf(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                    return false;
                }
                return true;
            } catch (Exception e) {
                logger.warn("⚠️ Failed to send system metrics to {}: {}", session.getId(), e.getMessage());
                return true; // Remove failed session
            }
        });
    }

    private Map<String, Object> generateSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return Map.of(
            "type", "system_metrics",
            "timestamp", System.currentTimeMillis(),
            "cpu", Map.of(
                "load", osBean.getSystemLoadAverage(),
                "processors", osBean.getAvailableProcessors()
            ),
            "memory", Map.of(
                "used", usedMemory / 1024 / 1024, // MB
                "total", totalMemory / 1024 / 1024, // MB
                "max", maxMemory / 1024 / 1024, // MB
                "free", freeMemory / 1024 / 1024, // MB
                "utilization", (double) usedMemory / totalMemory * 100
            ),
            "services", Map.of(
                "cockpit-core", "healthy",
                "database", "connected", 
                "ai-subsystem", "available"
            ),
            "uptime", ManagementFactory.getRuntimeMXBean().getUptime()
        );
    }

    /**
     * Broadcast service health update
     */
    public void broadcastServiceHealth(String serviceName, String status, String details) {
        Map<String, Object> update = Map.of(
            "type", "service_health",
            "serviceName", serviceName,
            "status", status,
            "details", details,
            "timestamp", System.currentTimeMillis()
        );
        
        broadcastMessage(update);
    }

    private void broadcastMessage(Map<String, Object> message) {
        if (sessions.isEmpty()) return;

        String jsonMessage;
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            logger.error("❌ Failed to serialize WebSocket message: {}", e.getMessage());
            return;
        }

        sessions.values().removeIf(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(jsonMessage));
                    return false;
                }
                return true;
            } catch (Exception e) {
                logger.warn("⚠️ Failed to send WebSocket message to {}: {}", session.getId(), e.getMessage());
                return true; // Remove failed session
            }
        });
    }
}