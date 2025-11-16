package com.devvault.frontend.services;

import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.dto.SecurityStatusDto;
import com.devvault.frontend.dto.ThreatDetectionDto;
import com.devvault.frontend.dto.EncryptionStatusDto;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise Security Service
 * Handles security operations, threat detection, and encryption management
 */
public class SecurityService {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    
    private final HttpClientService httpClient;
    private final ObservableList<ThreatDetectionDto> threats;
    
    // Security Status Properties
    private final BooleanProperty encryptionEnabled = new SimpleBooleanProperty(false);
    private final BooleanProperty threatDetectionActive = new SimpleBooleanProperty(false);
    private final IntegerProperty securityScore = new SimpleIntegerProperty(0);
    private final StringProperty securityStatus = new SimpleStringProperty("Unknown");
    
    private static SecurityService instance;
    
    private SecurityService() {
        this.httpClient = HttpClientService.getInstance();
        this.threats = FXCollections.observableArrayList();
        logger.info("Enterprise SecurityService initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized SecurityService getInstance() {
        if (instance == null) {
            instance = new SecurityService();
        }
        return instance;
    }
    
    // ============================================
    // SECURITY STATUS MANAGEMENT
    // ============================================
    
    /**
     * Get current security status from backend
     */
    public CompletableFuture<SecurityStatusDto> getSecurityStatus() {
        return httpClient.getBackendCoreAsync("/api/security/status", SecurityStatusDto.class)
                .thenApply(status -> {
                    // Update local properties
                    javafx.application.Platform.runLater(() -> {
                        encryptionEnabled.set(status.isEncryptionEnabled());
                        threatDetectionActive.set(status.isThreatDetectionActive());
                        securityScore.set((int) status.getSecurityScore());
                        securityStatus.set(status.getStatus());
                    });
                    return status;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get security status", throwable);
                    // Return default status on failure
                    return new SecurityStatusDto();
                });
    }
    
    /**
     * Run comprehensive security scan
     */
    public CompletableFuture<SecurityStatusDto> runSecurityScan() {
        logger.info("Starting comprehensive security scan");
        
        return httpClient.postBackendCoreAsync("/api/security/scan", null, SecurityStatusDto.class)
                .thenApply(result -> {
                    logger.info("Security scan completed - Score: {}", result.getSecurityScore());
                    
                    javafx.application.Platform.runLater(() -> {
                        securityScore.set((int) result.getSecurityScore());
                        securityStatus.set(result.getStatus());
                    });
                    
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("Security scan failed", throwable);
                    throw new RuntimeException("Security scan failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Enable/disable encryption system
     */
    public CompletableFuture<Boolean> setEncryptionEnabled(boolean enabled) {
        logger.info("Setting encryption enabled: {}", enabled);
        
        EncryptionConfigRequest request = new EncryptionConfigRequest();
        request.setEnabled(enabled);
        
        return httpClient.postBackendCoreAsync("/api/security/encryption/configure", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            encryptionEnabled.set(enabled);
                        });
                        logger.info("Encryption configuration updated successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to configure encryption", throwable);
                    return false;
                });
    }
    
    // ============================================
    // THREAT DETECTION
    // ============================================
    
    /**
     * Get active threats
     */
    public ObservableList<ThreatDetectionDto> getActiveThreats() {
        return threats;
    }
    
    /**
     * Refresh threats from backend
     */
    public CompletableFuture<Void> refreshThreats() {
        return httpClient.getBackendCoreAsync("/api/security/threats", new TypeReference<List<ThreatDetectionDto>>() {})
                .thenAccept(threatList -> {
                    javafx.application.Platform.runLater(() -> {
                        threats.clear();
                        threats.addAll(threatList);
                    });
                    logger.info("Refreshed {} active threats", threatList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh threats", throwable);
                    return null;
                });
    }
    
    /**
     * Run threat analysis on specific project
     */
    public CompletableFuture<ThreatDetectionDto> analyzeThreat(Long projectId) {
        logger.info("Analyzing threats for project: {}", projectId);
        
        return httpClient.postBackendCoreAsync("/api/security/threats/analyze/" + projectId, null, ThreatDetectionDto.class)
                .thenApply(threat -> {
                    if (threat.getSeverity().equals("HIGH") || threat.getSeverity().equals("CRITICAL")) {
                        javafx.application.Platform.runLater(() -> {
                            threats.add(threat);
                        });
                    }
                    return threat;
                })
                .exceptionally(throwable -> {
                    logger.error("Threat analysis failed for project: {}", projectId, throwable);
                    throw new RuntimeException("Threat analysis failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Quarantine identified threat
     */
    public CompletableFuture<Boolean> quarantineThreat(String threatId) {
        logger.info("Quarantining threat: {}", threatId);
        
        return httpClient.postBackendCoreAsync("/api/security/threats/" + threatId + "/quarantine", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            threats.removeIf(threat -> threat.getId().equals(threatId));
                        });
                        logger.info("Threat quarantined successfully: {}", threatId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to quarantine threat: {}", threatId, throwable);
                    return false;
                });
    }
    
    // ============================================
    // FILE INTEGRITY
    // ============================================
    
    /**
     * Run file integrity verification
     */
    public CompletableFuture<IntegrityReportDto> runIntegrityCheck(String projectPath) {
        logger.info("Running integrity check for: {}", projectPath);
        
        IntegrityCheckRequest request = new IntegrityCheckRequest();
        request.setProjectPath(projectPath);
        
        return httpClient.postBackendCoreAsync("/api/security/integrity/check", request, IntegrityReportDto.class)
                .thenApply(report -> {
                    logger.info("Integrity check completed - {} files verified", report.getTotalFiles());
                    return report;
                })
                .exceptionally(throwable -> {
                    logger.error("Integrity check failed for: {}", projectPath, throwable);
                    throw new RuntimeException("Integrity check failed: " + throwable.getMessage(), throwable);
                });
    }
    
    // ============================================
    // MACHINE LEARNING MODELS
    // ============================================
    
    /**
     * Update ML models for threat detection
     */
    public CompletableFuture<Boolean> updateMLModels() {
        logger.info("Updating ML models for threat detection");
        
        return httpClient.postBackendCoreAsync("/api/security/ml/update", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("ML models updated successfully");
                        // Refresh threat detection status
                        getSecurityStatus();
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to update ML models", throwable);
                    return false;
                });
    }
    
    /**
     * Analyze processes for threats using AI
     */
    public CompletableFuture<List<ProcessThreatDto>> analyzeProcesses() {
        logger.info("Analyzing running processes for threats");
        
        return httpClient.getAiSubsystemAsync("/api/security/process-analysis", new TypeReference<List<ProcessThreatDto>>() {})
                .thenApply(threats -> {
                    logger.info("Process analysis completed - {} potential threats found", threats.size());
                    return threats;
                })
                .exceptionally(throwable -> {
                    logger.error("Process threat analysis failed", throwable);
                    return List.of(); // Return empty list on failure
                });
    }
    
    // ============================================
    // SECURITY CONFIGURATION
    // ============================================
    
    /**
     * Enable zero-trust network security
     */
    public CompletableFuture<Boolean> enableZeroTrust() {
        logger.info("Enabling zero-trust network security");
        
        return httpClient.postBackendCoreAsync("/api/security/zero-trust/enable", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Zero-trust security enabled successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to enable zero-trust security", throwable);
                    return false;
                });
    }
    
    /**
     * Rotate encryption keys
     */
    public CompletableFuture<Boolean> rotateEncryptionKeys() {
        logger.info("Rotating encryption keys");
        
        return httpClient.postBackendCoreAsync("/api/security/keys/rotate", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Encryption keys rotated successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to rotate encryption keys", throwable);
                    return false;
                });
    }
    
    /**
     * Update threat definitions
     */
    public CompletableFuture<Boolean> updateThreatDefinitions() {
        logger.info("Updating threat definitions from security databases");
        
        return httpClient.postBackendCoreAsync("/api/security/threat-definitions/update", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Threat definitions updated successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to update threat definitions", throwable);
                    return false;
                });
    }
    
    // ============================================
    // PROPERTY ACCESSORS
    // ============================================
    
    public BooleanProperty encryptionEnabledProperty() { return encryptionEnabled; }
    public boolean isEncryptionEnabled() { return encryptionEnabled.get(); }
    
    public BooleanProperty threatDetectionActiveProperty() { return threatDetectionActive; }
    public boolean isThreatDetectionActive() { return threatDetectionActive.get(); }
    
    public IntegerProperty securityScoreProperty() { return securityScore; }
    public int getSecurityScore() { return securityScore.get(); }
    
    public StringProperty securityStatusProperty() { return securityStatus; }
    public String getSecurityStatusValue() { return securityStatus.get(); }
    
    // ============================================
    // DTO CLASSES
    // ============================================
    
    private static class EncryptionConfigRequest {
        private boolean enabled;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    private static class IntegrityCheckRequest {
        private String projectPath;
        
        public String getProjectPath() { return projectPath; }
        public void setProjectPath(String projectPath) { this.projectPath = projectPath; }
    }
    
    public static class IntegrityReportDto {
        private int totalFiles;
        private int verifiedFiles;
        private int corruptedFiles;
        private List<String> corruptedPaths;
        
        public int getTotalFiles() { return totalFiles; }
        public void setTotalFiles(int totalFiles) { this.totalFiles = totalFiles; }
        
        public int getVerifiedFiles() { return verifiedFiles; }
        public void setVerifiedFiles(int verifiedFiles) { this.verifiedFiles = verifiedFiles; }
        
        public int getCorruptedFiles() { return corruptedFiles; }
        public void setCorruptedFiles(int corruptedFiles) { this.corruptedFiles = corruptedFiles; }
        
        public List<String> getCorruptedPaths() { return corruptedPaths; }
        public void setCorruptedPaths(List<String> corruptedPaths) { this.corruptedPaths = corruptedPaths; }
    }
    
    public static class ProcessThreatDto {
        private String processId;
        private String processName;
        private String threatType;
        private String severity;
        private String description;
        
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        
        public String getProcessName() { return processName; }
        public void setProcessName(String processName) { this.processName = processName; }
        
        public String getThreatType() { return threatType; }
        public void setThreatType(String threatType) { this.threatType = threatType; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}