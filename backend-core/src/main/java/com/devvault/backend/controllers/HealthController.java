package com.devvault.backend.controllers;

import com.devvault.backend.model.HealthResponse;
import com.devvault.backend.services.HealthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {
    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse resp = healthService.getHealth();
        log.debug("Health check responded: {}", resp);
        return ResponseEntity.ok(resp);
    }
}
