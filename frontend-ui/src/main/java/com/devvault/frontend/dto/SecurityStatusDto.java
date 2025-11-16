package com.devvault.frontend.dto;

import java.time.LocalDateTime;

/**
 * DTO for security status information
 */
public class SecurityStatusDto {
    private String id;
    private String status;
    private String level;
    private String description;
    private LocalDateTime timestamp;
    private int threatLevel;
    private boolean encrypted;
    
    // Constructors
    public SecurityStatusDto() {}
    
    public SecurityStatusDto(String id, String status, String level) {
        this.id = id;
        this.status = status;
        this.level = level;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public int getThreatLevel() { return threatLevel; }
    public void setThreatLevel(int threatLevel) { this.threatLevel = threatLevel; }
    
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    
    // Additional methods needed by services
    public boolean isEncryptionEnabled() {
        return this.encrypted;
    }
    
    public boolean isThreatDetectionActive() {
        return this.threatLevel > 0;
    }
    
    public double getSecurityScore() {
        return Math.max(0, 100 - (this.threatLevel * 10));
    }
}