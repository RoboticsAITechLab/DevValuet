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

    public AiBridgeClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
                .setConnectTimeout(Duration.ofSeconds(2))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
        // Default local URL; allow override via spring property 'ai.subsystem.url'
        this.aiBaseUrl = System.getProperty("ai.subsystem.url", "http://localhost:8000");
    }

    /**
     * Call the AI subsystem kill-switch endpoint to disable AI for the given timeout.
     * Returns the parsed response map on success or null on failure.
     */
    public Map disableAI(int timeoutSeconds) {
        String url = String.format("%s/ai/disable", aiBaseUrl);
        try {
            logger.info("Calling AI disable at {} with timeout {}", url, timeoutSeconds);
            Map resp = restTemplate.postForObject(url, Map.of("timeout_seconds", timeoutSeconds), Map.class);
            logger.info("AI disable response: {}", resp);
            return resp;
        } catch (RestClientException ex) {
            logger.warn("Failed to call AI disable: {}", ex.getMessage());
            return null;
        }
    }
}
