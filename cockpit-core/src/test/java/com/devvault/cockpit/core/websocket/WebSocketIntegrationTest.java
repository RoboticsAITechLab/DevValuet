package com.devvault.cockpit.core.websocket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for WebSocket endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;
    
    private StandardWebSocketClient client;
    private WebSocketSession projectSession;
    private WebSocketSession systemSession;
    
    @BeforeEach
    void setUp() {
        client = new StandardWebSocketClient();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        if (projectSession != null && projectSession.isOpen()) {
            projectSession.close();
        }
        if (systemSession != null && systemSession.isOpen()) {
            systemSession.close();
        }
    }
    
    @Test
    void testProjectWebSocketConnection() throws Exception {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();
        CountDownLatch messageLatch = new CountDownLatch(1);
        
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                connectionLatch.countDown();
            }
            
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                receivedMessage.set(message.getPayload().toString());
                messageLatch.countDown();
            }
            
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                exception.printStackTrace();
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                // Connection closed
            }
            
            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };
        
        // Connect to project WebSocket endpoint
        URI uri = URI.create("ws://localhost:" + port + "/ws/projects");
        projectSession = client.doHandshake(handler, null, uri).get(5, TimeUnit.SECONDS);
        
        // Wait for connection
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), "Connection should be established");
        assertTrue(projectSession.isOpen(), "Session should be open");
        
        // Send a test message
        String testMessage = "{\"type\":\"test\",\"data\":\"project test message\"}";
        projectSession.sendMessage(new TextMessage(testMessage));
        
        // The handler should echo or process the message
        // Note: In real implementation, the handler would process and potentially broadcast
        // For this test, we verify the connection works
        Thread.sleep(1000); // Give time for message processing
        
        assertTrue(projectSession.isOpen(), "Session should remain open after message");
    }
    
    @Test
    void testSystemWebSocketConnection() throws Exception {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        CountDownLatch messageLatch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();
        
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                connectionLatch.countDown();
            }
            
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                receivedMessage.set(message.getPayload().toString());
                messageLatch.countDown();
            }
            
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                exception.printStackTrace();
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                // Connection closed
            }
            
            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };
        
        // Connect to system WebSocket endpoint
        URI uri = URI.create("ws://localhost:" + port + "/ws/system");
        systemSession = client.doHandshake(handler, null, uri).get(5, TimeUnit.SECONDS);
        
        // Wait for connection
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), "Connection should be established");
        assertTrue(systemSession.isOpen(), "Session should be open");
        
        // Wait for potential system metrics broadcast
        // The SystemStatusWebSocketHandler should start broadcasting metrics
        boolean messageReceived = messageLatch.await(10, TimeUnit.SECONDS);
        
        if (messageReceived) {
            assertNotNull(receivedMessage.get(), "Should have received a system metrics message");
            assertTrue(receivedMessage.get().contains("system_metrics") || 
                      receivedMessage.get().contains("type"), 
                      "Message should contain expected system metrics structure");
        }
        
        assertTrue(systemSession.isOpen(), "Session should remain open");
    }
    
    @Test
    void testMultipleConnectionsToSameEndpoint() throws Exception {
        CountDownLatch connection1Latch = new CountDownLatch(1);
        CountDownLatch connection2Latch = new CountDownLatch(1);
        
        WebSocketHandler handler1 = createTestHandler(connection1Latch);
        WebSocketHandler handler2 = createTestHandler(connection2Latch);
        
        URI uri = URI.create("ws://localhost:" + port + "/ws/projects");
        
        // Create two connections to the same endpoint
        WebSocketSession session1 = client.doHandshake(handler1, null, uri).get(5, TimeUnit.SECONDS);
        WebSocketSession session2 = client.doHandshake(handler2, null, uri).get(5, TimeUnit.SECONDS);
        
        try {
            // Both connections should be established
            assertTrue(connection1Latch.await(5, TimeUnit.SECONDS), "First connection should be established");
            assertTrue(connection2Latch.await(5, TimeUnit.SECONDS), "Second connection should be established");
            
            assertTrue(session1.isOpen(), "First session should be open");
            assertTrue(session2.isOpen(), "Second session should be open");
            
        } finally {
            if (session1.isOpen()) session1.close();
            if (session2.isOpen()) session2.close();
        }
    }
    
    @Test
    void testWebSocketConnectionRejectionForInvalidPath() {
        // Try to connect to invalid WebSocket path
        URI uri = URI.create("ws://localhost:" + port + "/ws/invalid");
        
        assertThrows(Exception.class, () -> {
            client.doHandshake(createTestHandler(new CountDownLatch(1)), null, uri)
                  .get(5, TimeUnit.SECONDS);
        }, "Connection to invalid path should fail");
    }
    
    @Test 
    void testWebSocketConnectionCleanup() throws Exception {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        CountDownLatch closeLatch = new CountDownLatch(1);
        
        WebSocketHandler handler = new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                connectionLatch.countDown();
            }
            
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                // Handle message
            }
            
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                exception.printStackTrace();
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                closeLatch.countDown();
            }
            
            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };
        
        URI uri = URI.create("ws://localhost:" + port + "/ws/projects");
        projectSession = client.doHandshake(handler, null, uri).get(5, TimeUnit.SECONDS);
        
        // Wait for connection
        assertTrue(connectionLatch.await(5, TimeUnit.SECONDS), "Connection should be established");
        
        // Close the connection
        projectSession.close();
        
        // Wait for close confirmation
        assertTrue(closeLatch.await(5, TimeUnit.SECONDS), "Connection should be closed cleanly");
        assertFalse(projectSession.isOpen(), "Session should be closed");
    }
    
    private WebSocketHandler createTestHandler(CountDownLatch connectionLatch) {
        return new WebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                connectionLatch.countDown();
            }
            
            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                // Handle message
            }
            
            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
                exception.printStackTrace();
            }
            
            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                // Connection closed
            }
            
            @Override
            public boolean supportsPartialMessages() {
                return false;
            }
        };
    }
}