package com.devvault.frontend.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise Authentication Manager
 * Handles JWT token management, GitHub OAuth, and secure API authentication
 * for frontend-backend communication
 */
public class AuthenticationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationManager.class);
    private static final String JWT_SECRET = "devvault-pro-x-enterprise-jwt-secret-key-2024-super-secure";
    
    private static AuthenticationManager instance;
    private String authToken;
    private String refreshToken;
    private LocalDateTime tokenExpiry;
    private boolean isAuthenticated = false;
    private final ObjectMapper objectMapper;
    
    private AuthenticationManager() {
        this.objectMapper = new ObjectMapper();
        logger.info("Authentication Manager initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }
    
    /**
     * Get current authentication token
     */
    public String getAuthToken() {
        if (authToken != null && isTokenValid()) {
            return authToken;
        }
        return null;
    }
    
    /**
     * Set authentication token
     */
    public void setAuthToken(String token) {
        this.authToken = token;
        this.tokenExpiry = parseTokenExpiry(token);
        this.isAuthenticated = true;
        logger.info("Authentication token set successfully");
    }
    
    /**
     * Set refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        logger.debug("Refresh token updated");
    }
    
    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return isAuthenticated && isTokenValid();
    }
    
    /**
     * Check if current token is valid
     */
    private boolean isTokenValid() {
        if (authToken == null || tokenExpiry == null) {
            return false;
        }
        return LocalDateTime.now().isBefore(tokenExpiry.minusMinutes(5)); // 5 min buffer
    }
    
    /**
     * Parse token expiry from JWT
     */
    private LocalDateTime parseTokenExpiry(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }
            
            // Parse JWT without verification for expiry check
            String[] chunks = token.split("\\.");
            if (chunks.length != 3) {
                logger.warn("Invalid JWT format");
                return null;
            }
            
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]));
            JsonNode claims = objectMapper.readTree(payload);
            
            if (claims.has("exp")) {
                long exp = claims.get("exp").asLong();
                return LocalDateTime.ofEpochSecond(exp, 0, java.time.ZoneOffset.UTC);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to parse token expiry", e);
        }
        
        // Default to 24 hours from now if parsing fails
        return LocalDateTime.now().plusHours(24);
    }
    
    /**
     * Authenticate with GitHub OAuth
     */
    public CompletableFuture<Boolean> authenticateWithGitHub(String authorizationCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("Starting GitHub OAuth authentication");
                
                // Exchange authorization code for access token
                // This will call cockpit-core OAuth endpoint
                String tokenUrl = HttpClientConfig.COCKPIT_CORE_BASE_URL + "/auth/github/token";
                
                // Create token exchange request
                GitHubTokenRequest tokenRequest = new GitHubTokenRequest();
                tokenRequest.setCode(authorizationCode);
                
                GitHubTokenResponse response = HttpClientConfig.getInstance()
                        .getRestTemplate()
                        .postForObject(tokenUrl, tokenRequest, GitHubTokenResponse.class);
                
                if (response != null && response.getAccessToken() != null) {
                    setAuthToken(response.getAccessToken());
                    if (response.getRefreshToken() != null) {
                        setRefreshToken(response.getRefreshToken());
                    }
                    
                    logger.info("GitHub OAuth authentication successful");
                    return true;
                } else {
                    logger.error("GitHub OAuth authentication failed - no access token received");
                    return false;
                }
                
            } catch (Exception e) {
                logger.error("GitHub OAuth authentication error", e);
                return false;
            }
        });
    }
    
    /**
     * Refresh authentication token
     */
    public CompletableFuture<Boolean> refreshAuthToken() {
        return CompletableFuture.supplyAsync(() -> {
            if (refreshToken == null || refreshToken.isEmpty()) {
                logger.warn("No refresh token available for token refresh");
                return false;
            }
            
            try {
                logger.info("Refreshing authentication token");
                
                String refreshUrl = HttpClientConfig.COCKPIT_CORE_BASE_URL + "/auth/refresh";
                
                RefreshTokenRequest request = new RefreshTokenRequest();
                request.setRefreshToken(refreshToken);
                
                GitHubTokenResponse response = HttpClientConfig.getInstance()
                        .getRestTemplate()
                        .postForObject(refreshUrl, request, GitHubTokenResponse.class);
                
                if (response != null && response.getAccessToken() != null) {
                    setAuthToken(response.getAccessToken());
                    logger.info("Token refresh successful");
                    return true;
                } else {
                    logger.error("Token refresh failed");
                    return false;
                }
                
            } catch (Exception e) {
                logger.error("Token refresh error", e);
                return false;
            }
        });
    }
    
    /**
     * Create enterprise session token
     */
    public String createSessionToken(String userId, String username) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            
            return Jwts.builder()
                    .subject(userId)
                    .claim("username", username)
                    .claim("client", "DevVaultProX")
                    .issuedAt(new java.util.Date())
                    .expiration(new java.util.Date(System.currentTimeMillis() + 86400000)) // 24 hours
                    .signWith(key)
                    .compact();
                    
        } catch (Exception e) {
            logger.error("Failed to create session token", e);
            return null;
        }
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return false;
            }
            
            SecretKey key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getExpiration().after(new java.util.Date());
            
        } catch (Exception e) {
            logger.warn("Token validation failed", e);
            return false;
        }
    }
    
    /**
     * Logout and clear authentication
     */
    public void logout() {
        this.authToken = null;
        this.refreshToken = null;
        this.tokenExpiry = null;
        this.isAuthenticated = false;
        logger.info("User logged out successfully");
    }
    
    /**
     * GitHub Token Request DTO
     */
    private static class GitHubTokenRequest {
        private String code;
        
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }
    
    /**
     * GitHub Token Response DTO
     */
    private static class GitHubTokenResponse {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        
        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
        
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }
        
        public Long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    }
    
    /**
     * Refresh Token Request DTO
     */
    private static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }
}