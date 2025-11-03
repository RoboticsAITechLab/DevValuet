package com.devvault.backend.model;

/**
 * Simple immutable DTO for health check responses.
 * Using a Java record keeps the code concise and thread-safe.
 */
public record HealthResponse(
        String status,
        String timestamp,
        long uptimeSeconds,
        String application,
        String version
) {
}
