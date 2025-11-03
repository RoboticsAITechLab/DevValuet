package com.devvault.backend.services.impl;

import com.devvault.backend.model.HealthResponse;
import com.devvault.backend.services.HealthService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class HealthServiceImpl implements HealthService {
    private final Instant startedAt = Instant.now();
    private final Environment env;

    public HealthServiceImpl(Environment env) {
        this.env = env;
    }

    @Override
    public HealthResponse getHealth() {
        String app = env.getProperty("spring.application.name", "devvault");
        String version = env.getProperty("application.version", env.getProperty("info.app.version", "unknown"));
        long uptime = Duration.between(startedAt, Instant.now()).getSeconds();
        return new HealthResponse("online", Instant.now().toString(), uptime, app, version);
    }
}
