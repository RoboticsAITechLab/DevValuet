package com.devvault.frontend.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * WebClient Request Filter for Reactive HTTP Operations
 * Provides logging, monitoring, and authentication for reactive HTTP calls
 */
public class WebClientRequestFilter implements ExchangeFilterFunction {
    
    private static final Logger logger = LoggerFactory.getLogger(WebClientRequestFilter.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        
        long startTime = System.currentTimeMillis();
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String requestId = generateRequestId();
        
        // Add enterprise headers
        ClientRequest enhancedRequest = ClientRequest.from(request)
                .header("X-Request-ID", requestId)
                .header("X-Client-Version", "DevVaultProX-1.0.0")
                .header("X-Client-Platform", "JavaFX-Desktop")
                .header("X-Request-Timestamp", String.valueOf(System.currentTimeMillis()))
                .headers(headers -> {
                    // Add authentication header if available
                    String authToken = AuthenticationManager.getInstance().getAuthToken();
                    if (authToken != null && !authToken.isEmpty()) {
                        headers.add("Authorization", "Bearer " + authToken);
                    }
                })
                .build();
        
        // Log outgoing request
        logger.info("→ [{}] {} {} {}", 
                requestId,
                timestamp,
                enhancedRequest.method(), 
                enhancedRequest.url());
        
        return next.exchange(enhancedRequest)
                .doOnNext(response -> {
                    long duration = System.currentTimeMillis() - startTime;
                    
                    // Log response
                    logger.info("← [{}] {} {} - Duration: {}ms - Status: {}", 
                            requestId,
                            LocalDateTime.now().format(TIME_FORMATTER),
                            enhancedRequest.url(),
                            duration,
                            response.statusCode());
                    
                    // Check for errors
                    if (!response.statusCode().is2xxSuccessful()) {
                        logger.warn("Non-successful reactive response [{}]: {} {} - Status: {}", 
                                requestId,
                                enhancedRequest.method(),
                                enhancedRequest.url(), 
                                response.statusCode());
                    }
                })
                .doOnError(error -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logger.error("✗ [{}] Reactive request failed after {}ms - {} {} - Error: {}", 
                            requestId,
                            duration,
                            enhancedRequest.method(),
                            enhancedRequest.url(),
                            error.getMessage());
                });
    }
    
    /**
     * Generate unique request ID for tracking
     */
    private String generateRequestId() {
        return "RRQ-" + System.currentTimeMillis() + "-" + 
               Integer.toHexString((int) (Math.random() * 0x1000));
    }
}