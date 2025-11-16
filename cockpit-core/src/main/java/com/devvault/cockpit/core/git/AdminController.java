package com.devvault.cockpit.core.git;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final GitHubOAuthStateCleanupService cleanupService;

    public AdminController(GitHubOAuthStateCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    /**
     * Trigger an immediate purge of expired OAuth states and return number of deleted rows.
     */
    @PostMapping("/oauth/states/purge")
    public ResponseEntity<?> purgeExpiredStates() {
        long deleted = cleanupService.purgeExpiredStatesNow();
        java.util.Map<String,Object> resp = java.util.Map.of("deletedCount", deleted);
        return ResponseEntity.ok(resp);
    }
}
