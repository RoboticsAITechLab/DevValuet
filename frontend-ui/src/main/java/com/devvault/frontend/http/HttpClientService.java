package com.devvault.frontend.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise HTTP Client Service
 * 
 * This is the main service class that provides a clean, enterprise-grade API
 * for frontend controllers to communicate with Spring Boot backend services.
 * 
 * Features:
 * - Automatic authentication handling
 * - Connection pooling and timeouts
 * - Retry logic with exponential backoff
 * - Comprehensive error handling
 * - Request/response logging
 * - JSON serialization/deserialization
 * - Reactive and blocking operations
 * 
 * Usage in Controllers:
 * ```java
 * HttpClientService httpClient = HttpClientService.getInstance();
 * CompletableFuture<List<Project>> projects = httpClient.getAsync("/api/projects", new TypeReference<List<Project>>() {});
 * ```
 */
public class HttpClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);
    
    private static HttpClientService instance;
    private final HttpClientConfig httpConfig;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authManager;
    private final HttpClient httpClient;
    
    private HttpClientService() {
        this.httpConfig = HttpClientConfig.getInstance();
        this.restTemplate = httpConfig.getRestTemplate();
        this.webClient = httpConfig.getWebClient();
        this.objectMapper = httpConfig.getObjectMapper();
        this.authManager = AuthenticationManager.getInstance();
        this.httpClient = HttpClient.newHttpClient();
        
        logger.info("HTTP Client Service initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized HttpClientService getInstance() {
        if (instance == null) {
            instance = new HttpClientService();
        }
        return instance;
    }
    
    // ============================================
    // SYNC HTTP OPERATIONS
    // ============================================
    
    /**
     * Synchronous GET request
     */
    public <T> T get(String endpoint, Class<T> responseType, String baseUrl) {
        String url = baseUrl + endpoint;
        try {
            logger.debug("Executing GET: {}", url);
            return restTemplate.getForObject(url, responseType);
        } catch (Exception e) {
            logger.error("GET request failed for {}", url, e);
            throw new HttpServiceException("GET request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Synchronous GET request with generic type handling
     */
    public <T> T get(String endpoint, TypeReference<T> responseType, String baseUrl) {
        String url = baseUrl + endpoint;
        try {
            logger.debug("Executing GET with TypeReference: {}", url);
            String response = restTemplate.getForObject(url, String.class);
            return objectMapper.readValue(response, responseType);
        } catch (Exception e) {
            logger.error("GET request failed for {}", url, e);
            throw new HttpServiceException("GET request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Synchronous POST request
     */
    public <T, R> T post(String endpoint, R requestBody, Class<T> responseType, String baseUrl) {
        String url = baseUrl + endpoint;
        try {
            logger.debug("Executing POST: {}", url);
            return restTemplate.postForObject(url, requestBody, responseType);
        } catch (Exception e) {
            logger.error("POST request failed for {}", url, e);
            throw new HttpServiceException("POST request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Synchronous PUT request
     */
    public <T, R> T put(String endpoint, R requestBody, Class<T> responseType, String baseUrl) {
        String url = baseUrl + endpoint;
        try {
            logger.debug("Executing PUT: {}", url);
            HttpEntity<R> entity = new HttpEntity<>(requestBody, createHeaders());
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            logger.error("PUT request failed for {}", url, e);
            throw new HttpServiceException("PUT request failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Synchronous DELETE request
     */
    public void delete(String endpoint, String baseUrl) {
        String url = baseUrl + endpoint;
        try {
            logger.debug("Executing DELETE: {}", url);
            restTemplate.delete(url);
        } catch (Exception e) {
            logger.error("DELETE request failed for {}", url, e);
            throw new HttpServiceException("DELETE request failed: " + e.getMessage(), e);
        }
    }
    
    // ============================================
    // ASYNC HTTP OPERATIONS
    // ============================================
    
    /**
     * Asynchronous GET request
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, Class<T> responseType, String baseUrl) {
        return CompletableFuture.supplyAsync(() -> get(endpoint, responseType, baseUrl), 
                httpConfig.getExecutorService());
    }
    
    /**
     * Asynchronous GET request with TypeReference
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, TypeReference<T> responseType, String baseUrl) {
        return CompletableFuture.supplyAsync(() -> get(endpoint, responseType, baseUrl), 
                httpConfig.getExecutorService());
    }
    
    /**
     * Asynchronous POST request
     */
    public <T, R> CompletableFuture<T> postAsync(String endpoint, R requestBody, Class<T> responseType, String baseUrl) {
        return CompletableFuture.supplyAsync(() -> post(endpoint, requestBody, responseType, baseUrl), 
                httpConfig.getExecutorService());
    }
    
    /**
     * Asynchronous POST request with default base URL
     */
    public <T, R> CompletableFuture<T> postAsync(String fullUrl, R requestBody, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(fullUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                return objectMapper.readValue(response.body(), responseType);
            } catch (Exception e) {
                throw new RuntimeException("POST request failed", e);
            }
        }, httpConfig.getExecutorService());
    }
    
    /**
     * Asynchronous PUT request
     */
    public <T, R> CompletableFuture<T> putAsync(String endpoint, R requestBody, Class<T> responseType, String baseUrl) {
        return CompletableFuture.supplyAsync(() -> put(endpoint, requestBody, responseType, baseUrl), 
                httpConfig.getExecutorService());
    }
    
    /**
     * Asynchronous DELETE request
     */
    public CompletableFuture<Void> deleteAsync(String endpoint, String baseUrl) {
        return CompletableFuture.runAsync(() -> delete(endpoint, baseUrl), 
                httpConfig.getExecutorService());
    }
    
    // ============================================
    // REACTIVE HTTP OPERATIONS
    // ============================================
    
    /**
     * Reactive GET request
     */
    public <T> Mono<T> getReactive(String endpoint, Class<T> responseType, String baseUrl) {
        String url = baseUrl + endpoint;
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .doOnSuccess(result -> logger.debug("Reactive GET successful: {}", url))
                .doOnError(error -> logger.error("Reactive GET failed: {}", url, error));
    }
    
    /**
     * Reactive POST request
     */
    public <T, R> Mono<T> postReactive(String endpoint, R requestBody, Class<T> responseType, String baseUrl) {
        String url = baseUrl + endpoint;
        return webClient.post()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .doOnSuccess(result -> logger.debug("Reactive POST successful: {}", url))
                .doOnError(error -> logger.error("Reactive POST failed: {}", url, error));
    }
    
    // ============================================
    // CONVENIENCE METHODS FOR SPECIFIC BACKENDS
    // ============================================
    
    /**
     * Cockpit-Core service calls
     */
    public <T> T getCockpitCore(String endpoint, Class<T> responseType) {
        return get(endpoint, responseType, HttpClientConfig.COCKPIT_CORE_BASE_URL);
    }
    
    public <T> T getCockpitCore(String endpoint, TypeReference<T> responseType) {
        return get(endpoint, responseType, HttpClientConfig.COCKPIT_CORE_BASE_URL);
    }
    
    public <T> CompletableFuture<T> getCockpitCoreAsync(String endpoint, Class<T> responseType) {
        return getAsync(endpoint, responseType, HttpClientConfig.COCKPIT_CORE_BASE_URL);
    }
    
    public <T> CompletableFuture<T> getCockpitCoreAsync(String endpoint, TypeReference<T> responseType) {
        return getAsync(endpoint, responseType, HttpClientConfig.COCKPIT_CORE_BASE_URL);
    }
    
    public <T, R> T postCockpitCore(String endpoint, R requestBody, Class<T> responseType) {
        return post(endpoint, requestBody, responseType, HttpClientConfig.COCKPIT_CORE_BASE_URL);
    }
    
    public <T, R> CompletableFuture<T> postCockpitCoreAsync(String endpoint, R requestBody, Class<T> responseType) {
        return postAsync(endpoint, requestBody, responseType, HttpClientConfig.COCKPIT_CORE_BASE_URL);
    }
    
    /**
     * Backend-Core service calls
     */
    public <T> T getBackendCore(String endpoint, Class<T> responseType) {
        return get(endpoint, responseType, HttpClientConfig.BACKEND_CORE_BASE_URL);
    }
    
    public <T> T getBackendCore(String endpoint, TypeReference<T> responseType) {
        return get(endpoint, responseType, HttpClientConfig.BACKEND_CORE_BASE_URL);
    }
    
    public <T> CompletableFuture<T> getBackendCoreAsync(String endpoint, Class<T> responseType) {
        return getAsync(endpoint, responseType, HttpClientConfig.BACKEND_CORE_BASE_URL);
    }
    
    public <T> CompletableFuture<T> getBackendCoreAsync(String endpoint, TypeReference<T> responseType) {
        return getAsync(endpoint, responseType, HttpClientConfig.BACKEND_CORE_BASE_URL);
    }
    
    public <T, R> T postBackendCore(String endpoint, R requestBody, Class<T> responseType) {
        return post(endpoint, requestBody, responseType, HttpClientConfig.BACKEND_CORE_BASE_URL);
    }
    
    /**
     * AI Subsystem service calls
     */
    public <T> T getAiSubsystem(String endpoint, Class<T> responseType) {
        return get(endpoint, responseType, HttpClientConfig.AI_SUBSYSTEM_BASE_URL);
    }
    
    public <T> CompletableFuture<T> getAiSubsystemAsync(String endpoint, Class<T> responseType) {
        return getAsync(endpoint, responseType, HttpClientConfig.AI_SUBSYSTEM_BASE_URL);
    }
    
    // ============================================
    // HEALTH CHECK METHODS
    // ============================================
    
    /**
     * Test connectivity to all backend services
     */
    public CompletableFuture<HealthCheckResult> performHealthCheck() {
        return CompletableFuture.supplyAsync(() -> {
            HealthCheckResult result = new HealthCheckResult();
            
            try {
                // Test Cockpit-Core
                getCockpitCore("/actuator/health", Object.class);
                result.setCockpitCoreOnline(true);
                logger.info("Health Check: Cockpit-Core is ONLINE");
            } catch (Exception e) {
                result.setCockpitCoreOnline(false);
                logger.warn("Health Check: Cockpit-Core is OFFLINE - {}", e.getMessage());
            }
            
            try {
                // Test Backend-Core
                getBackendCore("/actuator/health", Object.class);
                result.setBackendCoreOnline(true);
                logger.info("Health Check: Backend-Core is ONLINE");
            } catch (Exception e) {
                result.setBackendCoreOnline(false);
                logger.warn("Health Check: Backend-Core is OFFLINE - {}", e.getMessage());
            }
            
            try {
                // Test AI Subsystem
                getAiSubsystem("/health", Object.class);
                result.setAiSubsystemOnline(true);
                logger.info("Health Check: AI-Subsystem is ONLINE");
            } catch (Exception e) {
                result.setAiSubsystemOnline(false);
                logger.warn("Health Check: AI-Subsystem is OFFLINE - {}", e.getMessage());
            }
            
            return result;
        }, httpConfig.getExecutorService());
    }
    
    // ============================================
    // UTILITY METHODS
    // ============================================
    
    /**
     * Create HTTP headers with authentication
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        
        String authToken = authManager.getAuthToken();
        if (authToken != null && !authToken.isEmpty()) {
            headers.setBearerAuth(authToken);
        }
        
        return headers;
    }
    
    /**
     * Shutdown HTTP client resources
     */
    public void shutdown() {
        httpConfig.shutdown();
        logger.info("HTTP Client Service shutdown");
    }
    
    // ============================================
    // HEALTH CHECK RESULT CLASS
    // ============================================
    
    public static class HealthCheckResult {
        private boolean cockpitCoreOnline;
        private boolean backendCoreOnline;
        private boolean aiSubsystemOnline;
        
        public boolean isCockpitCoreOnline() { return cockpitCoreOnline; }
        public void setCockpitCoreOnline(boolean cockpitCoreOnline) { this.cockpitCoreOnline = cockpitCoreOnline; }
        
        public boolean isBackendCoreOnline() { return backendCoreOnline; }
        public void setBackendCoreOnline(boolean backendCoreOnline) { this.backendCoreOnline = backendCoreOnline; }
        
        public boolean isAiSubsystemOnline() { return aiSubsystemOnline; }
        public void setAiSubsystemOnline(boolean aiSubsystemOnline) { this.aiSubsystemOnline = aiSubsystemOnline; }
        
        public boolean isAllOnline() {
            return cockpitCoreOnline && backendCoreOnline && aiSubsystemOnline;
        }
        
        public int getOnlineCount() {
            int count = 0;
            if (cockpitCoreOnline) count++;
            if (backendCoreOnline) count++;
            if (aiSubsystemOnline) count++;
            return count;
        }
    }
    
    // ============================================
    // COCKPIT CORE SPECIFIC METHODS
    // ============================================
    
    /**
     * Asynchronous DELETE request for Cockpit Core
     */
    public CompletableFuture<Void> deleteCockpitCoreAsync(String endpoint) {
        return deleteAsync(endpoint, httpConfig.getCockpitCoreBaseUrl());
    }
    
    /**
     * Asynchronous PUT request for Cockpit Core
     */
    public <T, R> CompletableFuture<R> putCockpitCoreAsync(String endpoint, T requestBody, Class<R> responseType) {
        return putAsync(endpoint, requestBody, responseType, httpConfig.getCockpitCoreBaseUrl());
    }
    
    // ============================================
    // TypeReference Overloads for Complex Types
    // ============================================
    
    /**
     * GET request with TypeReference for complex generic types
     */
    public <T> CompletableFuture<T> getAsync(String endpoint, TypeReference<T> typeRef) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
                return objectMapper.readValue(response.getBody(), typeRef);
            } catch (Exception e) {
                logger.error("GET request failed for {}: {}", endpoint, e.getMessage());
                throw new HttpServiceException("Failed to execute GET request", e);
            }
        });
    }
    
    /**
     * POST request with TypeReference for complex response types
     */
    public <T, R> CompletableFuture<R> postAsync(String endpoint, T requestBody, TypeReference<R> responseTypeRef) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<T> entity = new HttpEntity<>(requestBody, headers);
                
                ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
                return objectMapper.readValue(response.getBody(), responseTypeRef);
            } catch (Exception e) {
                logger.error("POST request failed for {}: {}", endpoint, e.getMessage());
                throw new HttpServiceException("Failed to execute POST request", e);
            }
        });
    }
    
    /**
     * AI Subsystem specific methods with TypeReference support
     */
    public <T> CompletableFuture<T> getAiSubsystemAsync(String endpoint, TypeReference<T> typeRef) {
        String fullUrl = httpConfig.getAiServiceBaseUrl() + endpoint;
        return getAsync(fullUrl, typeRef);
    }
    
    public <T, R> CompletableFuture<R> postAiSubsystemAsync(String endpoint, T requestBody, TypeReference<R> responseTypeRef) {
        String fullUrl = httpConfig.getAiServiceBaseUrl() + endpoint;
        return postAsync(fullUrl, requestBody, responseTypeRef);
    }
    
    public <T> CompletableFuture<T> postAiSubsystemAsync(String endpoint, Object requestBody, Class<T> responseType) {
        String fullUrl = httpConfig.getAiServiceBaseUrl() + endpoint;
        return postAsync(fullUrl, requestBody, responseType);
    }
    
    /**
     * Backend Core specific methods
     */
    public <T> CompletableFuture<T> postBackendCoreAsync(String endpoint, Object requestBody, Class<T> responseType) {
        String fullUrl = httpConfig.getBackendCoreBaseUrl() + endpoint;
        return postAsync(fullUrl, requestBody, responseType);
    }
    
    // ============================================
    // CUSTOM EXCEPTION CLASS
    // ============================================
    
    public static class HttpServiceException extends RuntimeException {
        public HttpServiceException(String message) {
            super(message);
        }
        
        public HttpServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}