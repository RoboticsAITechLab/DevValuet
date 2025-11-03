package com.devvault.backend.services;

import com.devvault.backend.model.HealthResponse;

public interface HealthService {
    /**
     * Returns a snapshot of application health information.
     */
    HealthResponse getHealth();
}
