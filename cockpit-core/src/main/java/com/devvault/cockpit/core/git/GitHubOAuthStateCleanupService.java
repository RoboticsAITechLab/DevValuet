package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class GitHubOAuthStateCleanupService {
    private static final Logger log = LoggerFactory.getLogger(GitHubOAuthStateCleanupService.class);

    private final GitHubOAuthStateRepository repo;
    private final long ttlSeconds;

    public GitHubOAuthStateCleanupService(GitHubOAuthStateRepository repo,
                                          @Value("${github.oauth.state.ttl-seconds:600}") long ttlSeconds) {
        this.repo = repo;
        this.ttlSeconds = ttlSeconds;
    }

    // runs every 5 minutes by default; configurable via property github.oauth.cleanup.rate-ms
    @Scheduled(fixedRateString = "${github.oauth.cleanup.rate-ms:300000}")
    public void purgeExpiredStates() {
        try {
            long deleted = purgeExpiredStatesNow();
            log.debug("Purged {} OAuth states", deleted);
        } catch (Exception e) {
            log.warn("Failed to purge expired OAuth states", e);
        }
    }

    /**
     * Purge expired states and return the number of rows deleted. Useful for admin-triggered purge.
     */
    public long purgeExpiredStatesNow() {
        Instant before = Instant.now().minusSeconds(ttlSeconds);
        try {
            long deleted = repo.deleteByCreatedAtBefore(before);
            return deleted;
        } catch (Exception e) {
            log.warn("Failed to purge expired OAuth states", e);
            return 0L;
        }
    }
}
