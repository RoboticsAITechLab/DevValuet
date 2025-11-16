package com.devvault.frontend.dto;

import java.time.LocalDateTime;

/**
 * DTO for threat detection information
 */
public class ThreatDetectionDto {
    private String id;
    private String threatType;
    private String severity;
    private String source;
    private String target;
    private String description;
    private LocalDateTime detectedAt;
    private boolean blocked;
    private String actionTaken;
    
    // Constructors
    public ThreatDetectionDto() {}
    
    public ThreatDetectionDto(String threatType, String severity, String source) {
        this.threatType = threatType;
        this.severity = severity;
        this.source = source;
        this.detectedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getThreatType() { return threatType; }
    public void setThreatType(String threatType) { this.threatType = threatType; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    
    public String getActionTaken() { return actionTaken; }
    public void setActionTaken(String actionTaken) { this.actionTaken = actionTaken; }
}