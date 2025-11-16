package com.devvault.frontend.dto;

import java.time.LocalDateTime;

/**
 * DTO for application health information
 */
public class ApplicationHealthDto {
    private String id;
    private String serviceName;
    private String status;
    private String version;
    private LocalDateTime lastChecked;
    private boolean healthy;
    private String healthDetails;
    private double responseTime;
    
    // Constructors
    public ApplicationHealthDto() {}
    
    public ApplicationHealthDto(String serviceName, String status, boolean healthy) {
        this.serviceName = serviceName;
        this.status = status;
        this.healthy = healthy;
        this.lastChecked = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public LocalDateTime getLastChecked() { return lastChecked; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }
    
    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }
    
    public String getHealthDetails() { return healthDetails; }
    public void setHealthDetails(String healthDetails) { this.healthDetails = healthDetails; }
    
    public double getResponseTime() { return responseTime; }
    public void setResponseTime(double responseTime) { this.responseTime = responseTime; }
    
    // Additional methods needed by services
    public String getOverallStatus() {
        return this.healthy ? "HEALTHY" : "UNHEALTHY";
    }
}