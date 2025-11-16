package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GitHubService {
    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);

    @Value("${github.client.id:}")
    private String clientId;

    @Value("${github.client.secret:}")
    private String clientSecret;

    @Value("${github.redirect.uri:http://localhost:8085/api/git/github/callback}")
    private String redirectUri;

    @Value("${github.oauth.token-url:https://github.com/login/oauth/access_token}")
    private String tokenUrl;

    @Value("${github.api.base-url:https://api.github.com}")
    private String apiBaseUrl;

    private final GitHubConnectionRepository repo;
    private final GitTokenEncryptor encryptor;
    private final GitHubConnectionScopeRepository scopeRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public GitHubService(GitHubConnectionRepository repo, GitTokenEncryptor encryptor, GitHubConnectionScopeRepository scopeRepo) {
        this.repo = repo;
        this.encryptor = encryptor;
        this.scopeRepo = scopeRepo;
    }

    public String buildAuthorizeUrl(Long projectId, String state) {
        // scope repo to allow repo access; in minimal case use read-only scopes
        return String.format("https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=repo&state=%s&allow_signup=true", clientId, redirectUri, state);
    }

    /**
     * Exchange code for access token using GitHub OAuth. Returns access_token or null on failure.
     */
    public String exchangeCodeForToken(String code) {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            log.warn("GitHub client id/secret not configured");
            return null;
        }
        try {
            HttpClient c = HttpClient.newHttpClient();
            var body = String.format("client_id=%s&client_secret=%s&code=%s&redirect_uri=%s", clientId, clientSecret, code, redirectUri);
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(tokenUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> resp = c.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.warn("GitHub token exchange failed: {} {}", resp.statusCode(), resp.body());
                return null;
            }
            Map m = mapper.readValue(resp.body(), Map.class);
            Object t = m.get("access_token");
            return t == null ? null : t.toString();
        } catch (Exception e) {
            log.error("token exchange error", e);
            return null;
        }
    }
    
    /**
     * Fetch provider user info and scopes using the provided access token.
     * Returns a map with keys: "providerUser" and "scopes" (comma-separated).
     */
    public java.util.Map<String,String> fetchProviderUserAndScopes(String accessToken) {
        try {
            HttpClient c = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
            .uri(URI.create(apiBaseUrl + "/user"))
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .header("Authorization", "token " + accessToken)
                    .GET()
                    .build();
            HttpResponse<String> resp = c.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                log.warn("GitHub user fetch failed: {} {}", resp.statusCode(), resp.body());
                return java.util.Collections.emptyMap();
            }
            Map m = mapper.readValue(resp.body(), Map.class);
            Object login = m.get("login");
            Object emailObj = m.get("email");
            // scopes are returned in headers like 'X-OAuth-Scopes' or 'X-Accepted-OAuth-Scopes'
            String scopesHeader = String.join(",", resp.headers().map().getOrDefault("x-oauth-scopes", java.util.List.of()).toArray(new String[0]));
            if (scopesHeader.isBlank()) {
                scopesHeader = String.join(",", resp.headers().map().getOrDefault("x-accepted-oauth-scopes", java.util.List.of()).toArray(new String[0]));
            }
            String providerEmail = emailObj == null ? "" : emailObj.toString();

            // if email is not present in /user, try /user/emails endpoint (requires user:email scope)
            if ((providerEmail == null || providerEmail.isBlank())) {
                try {
            HttpRequest req2 = HttpRequest.newBuilder()
                .uri(URI.create(apiBaseUrl + "/user/emails"))
                            .timeout(Duration.ofSeconds(10))
                            .header("Accept", "application/json")
                            .header("Authorization", "token " + accessToken)
                            .GET()
                            .build();
                    HttpResponse<String> resp2 = c.send(req2, HttpResponse.BodyHandlers.ofString());
                    if (resp2.statusCode() == 200) {
                        java.util.List<Map> emails = mapper.readValue(resp2.body(), java.util.List.class);
                        for (Map e : emails) {
                            Object primary = e.get("primary");
                            Object verified = e.get("verified");
                            Object em = e.get("email");
                            boolean isPrimary = primary instanceof Boolean ? (Boolean) primary : false;
                            boolean isVerified = verified instanceof Boolean ? (Boolean) verified : false;
                            if (isPrimary && isVerified && em != null) {
                                providerEmail = em.toString();
                                break;
                            }
                            if (providerEmail.isBlank() && em != null) {
                                providerEmail = em.toString();
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.debug("failed to fetch user emails", ex);
                }
            }

            // normalize scopes: split, trim, unique, sort
            String normalizedScopes = "";
            if (scopesHeader != null && !scopesHeader.isBlank()) {
                java.util.Set<String> s = new java.util.TreeSet<>();
                for (String sp : scopesHeader.split(",")) {
                    String t = sp == null ? "" : sp.trim();
                    if (!t.isBlank()) s.add(t);
                }
                normalizedScopes = String.join(",", s);
            }

            return java.util.Map.of(
                    "providerUser", login == null ? "" : login.toString(),
                    "scopes", normalizedScopes == null ? "" : normalizedScopes,
                    "providerEmail", providerEmail == null ? "" : providerEmail
            );
        } catch (Exception e) {
            log.error("fetch provider info error", e);
            return java.util.Collections.emptyMap();
        }
    }

    public GitHubConnection saveConnection(Long projectId, String providerUser, String providerEmail, String scopes, String accessToken) {
        String toStore = accessToken;
        if (encryptor != null && encryptor.isEnabled() && accessToken != null) {
            toStore = encryptor.encrypt(accessToken);
        }
        GitHubConnection g = new GitHubConnection(projectId, providerUser, toStore, scopes, providerEmail);
        GitHubConnection saved = repo.save(g);

        // persist normalized scopes into the scopes table
        if (scopeRepo != null) {
            try {
                scopeRepo.deleteByConnectionId(saved.getId());
                if (scopes != null && !scopes.isBlank()) {
                    for (String s : scopes.split(",")) {
                        String t = s == null ? "" : s.trim();
                        if (!t.isBlank()) {
                            scopeRepo.save(new GitHubConnectionScope(saved, t));
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("failed to persist normalized scopes", e);
            }
        }

        return saved;
    }

    public String getDecryptedToken(GitHubConnection conn) {
        if (conn == null) return null;
        String stored = conn.getAccessToken();
        if (encryptor != null && encryptor.isEnabled() && stored != null) {
            return encryptor.decrypt(stored);
        }
        return stored;
    }
}
