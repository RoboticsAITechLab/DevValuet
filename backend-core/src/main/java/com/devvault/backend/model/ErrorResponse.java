package com.devvault.backend.model;

import java.time.Instant;

/**
 * Standardized error response for API errors.
 */
public record ErrorResponse(
        String error,
        String message,
        String timestamp
) {
    public ErrorResponse(String error, String message) {
        this(error, message, Instant.now().toString());
    }
}
