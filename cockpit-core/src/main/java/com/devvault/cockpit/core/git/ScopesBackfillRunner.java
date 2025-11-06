package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "scopes.backfill.run", havingValue = "true")
public class ScopesBackfillRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ScopesBackfillRunner.class);

    private final GitHubConnectionRepository connRepo;
    private final GitHubConnectionScopeRepository scopeRepo;

    @Value("${scopes.backfill.dryRun:false}")
    private boolean dryRun = false;

    public ScopesBackfillRunner(GitHubConnectionRepository connRepo, GitHubConnectionScopeRepository scopeRepo) {
        this.connRepo = connRepo;
        this.scopeRepo = scopeRepo;
    }

    // package-private setter for tests
    void setDryRun(boolean dryRun) { this.dryRun = dryRun; }

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting scopes backfill run (dryRun={})", dryRun);
        List<GitHubConnection> all = connRepo.findAll();
        int filled = 0;
        int skipped = 0;
        for (GitHubConnection c : all) {
            String connScopes = c.getScopes();
            try {
                // clear any existing scopes for this connection
                if (!dryRun) scopeRepo.deleteByConnectionId(c.getId());

                if (connScopes == null || connScopes.isBlank()) {
                    skipped++;
                    continue;
                }
                String[] parts = connScopes.split(",");
                for (String p : parts) {
                    String t = p == null ? "" : p.trim();
                    if (t.isBlank()) continue;
                    if (dryRun) {
                        log.info("[dry-run] would add scope '{}' for connection {}", t, c.getId());
                    } else {
                        scopeRepo.save(new GitHubConnectionScope(c, t));
                    }
                }
                filled++;
            } catch (Exception e) {
                log.warn("Failed to backfill scopes for connection {}", c.getId(), e);
            }
        }
        log.info("Scopes backfill completed. filled={}, skipped={}", filled, skipped);
    }
}
