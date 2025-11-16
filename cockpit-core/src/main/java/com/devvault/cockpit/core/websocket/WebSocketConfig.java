package com.devvault.cockpit.core.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket configuration for DevVault Pro X real-time communication
 * Handles frontend ↔ backend real-time updates
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ProjectWebSocketHandler projectHandler;
    private final SystemStatusWebSocketHandler systemHandler;

    public WebSocketConfig(ProjectWebSocketHandler projectHandler, SystemStatusWebSocketHandler systemHandler) {
        this.projectHandler = projectHandler;
        this.systemHandler = systemHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Project updates channel
        registry.addHandler(projectHandler, "/ws/projects")
                .setAllowedOrigins("*"); // For development - restrict in production

        // System status updates channel  
        registry.addHandler(systemHandler, "/ws/system")
                .setAllowedOrigins("*"); // For development - restrict in production
    }
}