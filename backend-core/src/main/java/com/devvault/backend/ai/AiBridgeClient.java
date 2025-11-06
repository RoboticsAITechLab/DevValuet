package com.devvault.backend.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;

@Component
public class AiBridgeClient {
    private static final Logger logger = LoggerFactory.getLogger(AiBridgeClient.class);

    private final RestTemplate restTemplate;
    private final String aiBaseUrl;

    public AiBridgeClient(RestTemplateBuilder builder, @org.springframework.beans.factory.annotation.Value("${ai.subsystem.url:http://localhost:8000}") String aiBaseUrl) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        // Injected base URL (configurable via application.properties or system property)
        this.aiBaseUrl = aiBaseUrl;
    }

    /**
     * Call the AI subsystem kill-switch endpoint to disable AI for the given timeout.
     * Returns the parsed response map on success or null on failure.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> disableAI(int timeoutSeconds) {
        String url = String.format("%s/ai/disable", aiBaseUrl);
        try {
            logger.info("Calling AI disable at {} with timeout {}", url, timeoutSeconds);
            Map<String, Object> payload = java.util.Map.of("timeout_seconds", timeoutSeconds);
            Map<String, Object> resp = restTemplate.postForObject(url, payload, Map.class);
            if (resp == null) return java.util.Map.of("ok", false, "reason", "empty-response");
            logger.info("AI disable response: {}", resp);
            return resp;
        } catch (RestClientException ex) {
            logger.warn("Failed to call AI disable: {}", ex.getMessage());
            return java.util.Map.of("ok", false, "reason", ex.getMessage());
        }
    }
}
