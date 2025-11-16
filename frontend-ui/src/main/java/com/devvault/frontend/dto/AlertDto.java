package com.devvault.frontend.dto;

import java.time.LocalDateTime;

/**
 * DTO for alert information
 */
public class AlertDto {
    private String id;
    private String alertType;
    private String severity;
    private String message;
    private String source;
    private LocalDateTime createdAt;
    private boolean acknowledged;
    private String assignee;
    
    // Constructors
    public AlertDto() {}
    
    public AlertDto(String alertType, String severity, String message) {
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.acknowledged = false;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isAcknowledged() { return acknowledged; }
    public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
    
    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }
    
    // Additional methods needed by services
    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledged = true;
        this.assignee = acknowledgedBy;
    }
}