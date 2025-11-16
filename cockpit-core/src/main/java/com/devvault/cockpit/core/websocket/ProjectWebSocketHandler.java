package com.devvault.cockpit.core.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for project-related real-time updates
 * Broadcasts project creation, updates, build status changes
 */
@Component
public class ProjectWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProjectWebSocketHandler.class);
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        logger.info("🔌 Project WebSocket connected: {}", session.getId());
        
        // Send welcome message with current project count
        Map<String, Object> welcome = Map.of(
            "type", "connection",
            "message", "Connected to DevVault Pro X Project Updates",
            "timestamp", System.currentTimeMillis()
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(welcome)));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        logger.info("🔌 Project WebSocket disconnected: {} ({})", session.getId(), status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.debug("📨 Received project WebSocket message: {}", message.getPayload());
        
        // Echo back for now - extend with actual project commands
        Map<String, Object> response = Map.of(
            "type", "ack",
            "received", message.getPayload(),
            "timestamp", System.currentTimeMillis()
        );
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
    }

    /**
     * Broadcast project update to all connected clients
     */
    public void broadcastProjectUpdate(String projectName, String status, String details) {
        Map<String, Object> update = Map.of(
            "type", "project_update",
            "projectName", projectName,
            "status", status,
            "details", details,
            "timestamp", System.currentTimeMillis()
        );
        
        broadcast(update);
    }

    /**
     * Broadcast new project creation
     */
    public void broadcastNewProject(String projectName, String type) {
        Map<String, Object> update = Map.of(
            "type", "project_created",
            "projectName", projectName,
            "projectType", type,
            "timestamp", System.currentTimeMillis()
        );
        
        broadcast(update);
    }

    private void broadcast(Map<String, Object> message) {
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