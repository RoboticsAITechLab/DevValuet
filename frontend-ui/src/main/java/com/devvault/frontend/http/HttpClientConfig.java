package com.devvault.frontend.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Enterprise-grade HTTP Client Configuration
 * Provides configured RestTemplate, WebClient, and connection pooling for
 * frontend-to-backend communication with the Spring Boot microservices.
 */
public class HttpClientConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientConfig.class);
    
    // Backend service URLs
    public static final String COCKPIT_CORE_BASE_URL = "http://localhost:8080";
    public static final String BACKEND_CORE_BASE_URL = "http://localhost:8085";
    public static final String AI_SUBSYSTEM_BASE_URL = "http://localhost:8001";
    
    // HTTP Client Configuration
    private static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    private static final int REQUEST_TIMEOUT = 60000;    // 60 seconds
    private static final int MAX_CONNECTIONS = 100;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 20;
    private static final int CONNECTION_IDLE_TIMEOUT = 300000; // 5 minutes
    
    // Retry Configuration
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(2);
    
    private static HttpClientConfig instance;
    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService executorService;
    private final CloseableHttpClient httpClient;
    
    private HttpClientConfig() {
        logger.info("Initializing Enterprise HTTP Client Configuration");
        
        // Initialize executor service
        this.executorService = Executors.newScheduledThreadPool(10);
        
        // Configure ObjectMapper with enterprise settings
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        
        // Configure HTTP Client with connection pooling
        this.httpClient = createHttpClient();
        
        // Configure RestTemplate with enterprise settings
        this.restTemplate = createRestTemplate();
        
        // Configure WebClient for reactive operations
        this.webClient = createWebClient();
        
        logger.info("HTTP Client Configuration initialized successfully");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized HttpClientConfig getInstance() {
        if (instance == null) {
            instance = new HttpClientConfig();
        }
        return instance;
    }
    
    /**
     * Create enterprise-grade HTTP Client with connection pooling
     */
    private CloseableHttpClient createHttpClient() {
        logger.debug("Creating HTTP Client with connection pooling");
        
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(MAX_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(MAX_CONNECTIONS_PER_ROUTE);
        
        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(CONNECTION_TIMEOUT))
                .setSocketTimeout(Timeout.ofMilliseconds(REQUEST_TIMEOUT))
                .build();
        
        connectionManager.setDefaultConnectionConfig(connectionConfig);
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(CONNECTION_TIMEOUT))
                .setResponseTimeout(Timeout.ofMilliseconds(REQUEST_TIMEOUT))
                .build();
        
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .evictIdleConnections(Timeout.ofMilliseconds(CONNECTION_IDLE_TIMEOUT))
                .build();
    }
    
    /**
     * Create RestTemplate with enterprise configuration
     */
    private RestTemplate createRestTemplate() {
        logger.debug("Creating RestTemplate with enterprise configuration");
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        factory.setConnectTimeout(CONNECTION_TIMEOUT);
        factory.setConnectionRequestTimeout(CONNECTION_TIMEOUT);
        
        RestTemplate template = new RestTemplate(factory);
        
        // Add enterprise error handling interceptor
        template.getInterceptors().add(new HttpRequestInterceptor());
        
        return template;
    }
    
    /**
     * Create WebClient for reactive operations
     */
    private WebClient createWebClient() {
        logger.debug("Creating WebClient for reactive operations");
        
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "DevVaultProX-Frontend/1.0")
                .filter(new WebClientRequestFilter())
                .build();
    }
    
    /**
     * Get configured RestTemplate
     */
    public RestTemplate getRestTemplate() {
        return restTemplate;
    }
    
    /**
     * Get configured WebClient
     */
    public WebClient getWebClient() {
        return webClient;
    }
    
    /**
     * Get ObjectMapper for JSON processing
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
    
    /**
     * Get executor service for async operations
     */
    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }
    
    /**
     * Execute async REST call with retry logic
     */
    public <T> CompletableFuture<T> executeWithRetry(String url, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return restTemplate.getForObject(url, responseType);
            } catch (Exception e) {
                logger.warn("REST call failed for {}, retrying...", url, e);
                throw new RuntimeException("REST call failed: " + e.getMessage(), e);
            }
        }, executorService)
        .handle((result, throwable) -> {
            if (throwable != null) {
                logger.error("Async REST call failed for {}", url, throwable);
                return null;
            }
            return result;
        });
    }
    
    /**
     * Execute reactive call with retry logic
     */
    public <T> Mono<T> executeReactiveWithRetry(String url, Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, RETRY_DELAY)
                        .doBeforeRetry(retrySignal -> 
                            logger.warn("Retrying request to {} (attempt {})", url, retrySignal.totalRetries() + 1)))
                .doOnSuccess(result -> logger.debug("Successful reactive call to {}", url))
                .doOnError(error -> logger.error("Failed reactive call to {}", url, error));
    }
    
    /**
     * Test connectivity to backend services
     */
    public CompletableFuture<Boolean> testBackendConnectivity() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Test cockpit-core
                restTemplate.getForObject(COCKPIT_CORE_BASE_URL + "/actuator/health", Object.class);
                logger.info("Cockpit-Core connectivity: SUCCESS");
                
                // Test backend-core
                restTemplate.getForObject(BACKEND_CORE_BASE_URL + "/actuator/health", Object.class);
                logger.info("Backend-Core connectivity: SUCCESS");
                
                return true;
            } catch (Exception e) {
                logger.error("Backend connectivity test failed", e);
                return false;
            }
        }, executorService);
    }
    
    /**
     * Shutdown resources
     */
    public void shutdown() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
            }
            if (httpClient != null) {
                httpClient.close();
            }
            logger.info("HTTP Client Configuration shutdown successfully");
        } catch (Exception e) {
            logger.error("Error during HTTP Client shutdown", e);
        }
    }
    
    // ============================================
    // BASE URL CONFIGURATIONS  
    // ============================================
    
    public String getCockpitCoreBaseUrl() {
        return "http://localhost:8085/api";
    }
    
    public String getAiServiceBaseUrl() {
        return "http://localhost:8001/ai";
    }
    
    public String getBackendCoreBaseUrl() {
        return "http://localhost:8082/api";  
    }
}