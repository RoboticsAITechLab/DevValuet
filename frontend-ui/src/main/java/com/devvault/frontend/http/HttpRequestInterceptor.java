package com.devvault.frontend.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Enterprise HTTP Request Interceptor
 * Provides comprehensive logging, monitoring, and error tracking for all HTTP requests
 */
public class HttpRequestInterceptor implements ClientHttpRequestInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestInterceptor.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {
        
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String requestId = generateRequestId();
        
        // Log outgoing request
        logger.info("→ [{}] {} {} {} - Size: {} bytes", 
                requestId,
                timestamp,
                request.getMethod(), 
                request.getURI(),
                body != null ? body.length : 0);
        
        // Add enterprise headers
        addEnterpriseHeaders(request, requestId);
        
        try {
            // Execute request
            ClientHttpResponse response = execution.execute(request, body);
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Log response
            logger.info("← [{}] {} {} - Duration: {}ms - Status: {}", 
                    requestId,
                    LocalDateTime.now().format(TIME_FORMATTER),
                    request.getURI(),
                    duration,
                    response.getStatusCode());
            
            // Check for errors
            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.warn("Non-successful response [{}]: {} {} - Status: {} {}", 
                        requestId,
                        request.getMethod(),
                        request.getURI(), 
                        response.getStatusCode().value(),
                        response.getStatusText());
            }
            
            return response;
            
        } catch (IOException e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("✗ [{}] Request failed after {}ms - {} {} - Error: {}", 
                    requestId,
                    duration,
                    request.getMethod(),
                    request.getURI(),
                    e.getMessage());
            throw e;
        }
    }
    
    /**
     * Add enterprise-specific headers to all requests
     */
    private void addEnterpriseHeaders(HttpRequest request, String requestId) {
        request.getHeaders().add("X-Request-ID", requestId);
        request.getHeaders().add("X-Client-Version", "DevVaultProX-1.0.0");
        request.getHeaders().add("X-Client-Platform", "JavaFX-Desktop");
        request.getHeaders().add("X-Request-Timestamp", String.valueOf(System.currentTimeMillis()));
        
        // Add authentication header if available
        String authToken = AuthenticationManager.getInstance().getAuthToken();
        if (authToken != null && !authToken.isEmpty()) {
            request.getHeaders().add("Authorization", "Bearer " + authToken);
        }
    }
    
    /**
     * Generate unique request ID for tracking
     */
    private String generateRequestId() {
        return "REQ-" + System.currentTimeMillis() + "-" + 
               Integer.toHexString((int) (Math.random() * 0x1000));
    }
}