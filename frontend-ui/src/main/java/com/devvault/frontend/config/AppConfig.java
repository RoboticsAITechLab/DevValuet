package com.devvault.frontend.config;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simplified Application Configuration for DevVault Pro X
 * Java 21 Record-based configuration
 */
public record AppConfig(
    String version,
    Path sandboxPath,
    Path keystorePath,
    int aiSubsystemPort,
    UIConfig ui,
    SecurityConfig security
) {
    
    /**
     * UI Configuration record
     */
    public record UIConfig(
        int windowWidth,
        int windowHeight,
        boolean maximized,
        String defaultTheme
    ) {}
    
    /**
     * Security Configuration record
     */
    public record SecurityConfig(
        String keystorePassword,
        String algorithm,
        int keySize
    ) {}
    
    /**
     * Create default configuration
     */
    public static AppConfig createDefault() {
        var userHome = Paths.get(System.getProperty("user.home"));
        var sandboxPath = userHome.resolve(".devvault-pro");
        var keystorePath = sandboxPath.resolve("security").resolve("keystore.p12");
        
        var uiConfig = new UIConfig(1400, 900, false, "neo-dark");
        var securityConfig = new SecurityConfig("devvault-secure-2024", "AES", 256);
        
        return new AppConfig(
            "1.0.0-ENTERPRISE",
            sandboxPath,
            keystorePath,
            8765,
            uiConfig,
            securityConfig
        );
    }
    
    /**
     * Validate and initialize configuration
     */
    public AppConfig validateAndInitialize() {
        try {
            // Create sandbox directory if it doesn't exist
            java.nio.file.Files.createDirectories(sandboxPath);
            java.nio.file.Files.createDirectories(keystorePath.getParent());
            
            return this;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize configuration: " + e.getMessage(), e);
        }
    }
}