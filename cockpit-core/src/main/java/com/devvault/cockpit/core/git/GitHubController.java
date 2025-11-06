package com.devvault.cockpit.core.git;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class GitHubController {
    private final GitHubService gitHubService;
    private final GitHubConnectionRepository connectionRepository;
    private final GitHubOAuthStateRepository stateRepository;

    public GitHubController(GitHubService gitHubService, GitHubConnectionRepository connectionRepository, GitHubOAuthStateRepository stateRepository) {
        this.gitHubService = gitHubService;
        this.connectionRepository = connectionRepository;
        this.stateRepository = stateRepository;
    }

    @GetMapping("/api/projects/{projectId}/git/github/authorize")
    public ResponseEntity<?> authorize(@PathVariable("projectId") Long projectId) {
        // generate state and persist it for CSRF verification
        String state = UUID.randomUUID().toString();
        GitHubOAuthState s = new GitHubOAuthState(projectId, state);
        stateRepository.save(s);
        String url = gitHubService.buildAuthorizeUrl(projectId, state);
        return ResponseEntity.ok().body(java.util.Map.of("authorizeUrl", url, "state", state));
    }

    @PostMapping("/api/projects/{projectId}/git/github/callback")
    public ResponseEntity<?> callback(@PathVariable("projectId") Long projectId,
                                      @RequestParam("code") String code,
                                      @RequestParam(value = "state", required = false) String state) {
        // validate 'state' to protect against CSRF
        if (state == null || state.isBlank()) {
            return ResponseEntity.badRequest().body("missing state");
        }
        var opt = stateRepository.findByState(state);
        if (opt.isEmpty()) {
            return ResponseEntity.status(403).body("invalid or expired state");
        }
        GitHubOAuthState s = opt.get();
        // enforce TTL (10 minutes)
        java.time.Instant expiryThreshold = java.time.Instant.now().minus(java.time.Duration.ofMinutes(10));
        if (s.getCreatedAt().isBefore(expiryThreshold)) {
            // expired: delete and reject
            stateRepository.deleteByState(state);
            return ResponseEntity.status(403).body("state expired");
        }
        if (!projectId.equals(s.getProjectId())) {
            return ResponseEntity.status(403).body("state does not match project");
        }

        // exchange code for token
        String token = gitHubService.exchangeCodeForToken(code);
        if (token == null) {
            return ResponseEntity.status(502).body("failed to exchange code for token");
        }

        // fetch provider user, scopes and provider email and persist them along with token
        java.util.Map<String,String> info = gitHubService.fetchProviderUserAndScopes(token);
        String providerUser = info.getOrDefault("providerUser", "");
        String scopes = info.getOrDefault("scopes", "");
        String providerEmail = info.getOrDefault("providerEmail", "");

        GitHubConnection conn = gitHubService.saveConnection(projectId, providerUser, providerEmail, scopes, token);

        // remove used state
        stateRepository.deleteByState(state);

        java.util.Map<String,Object> resp = new java.util.HashMap<>();
        resp.put("id", conn.getId());
        resp.put("projectId", conn.getProjectId());
        resp.put("providerUser", conn.getProviderUser() == null ? "" : conn.getProviderUser());
        resp.put("providerEmail", conn.getProviderEmail() == null ? "" : conn.getProviderEmail());
        resp.put("scopes", conn.getScopes() == null ? "" : conn.getScopes());
        return ResponseEntity.ok().body(resp);
    }
}
