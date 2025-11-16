package com.devvault.frontend.dto;

import java.time.LocalDateTime;

/**
 * DTO for plugin metrics
 */
public class PluginMetricsDto {
    private String id;
    private String pluginId;
    private String name;
    private long executions;
    private long errors;
    private double averageExecutionTime;
    private LocalDateTime lastExecution;
    private String status;
    
    // Constructors
    public PluginMetricsDto() {}
    
    public PluginMetricsDto(String pluginId, String name) {
        this.pluginId = pluginId;
        this.name = name;
        this.status = "ACTIVE";
        this.lastExecution = LocalDateTime.now();
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPluginId() { return pluginId; }
    public void setPluginId(String pluginId) { this.pluginId = pluginId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getExecutions() { return executions; }
    public void setExecutions(long executions) { this.executions = executions; }
    
    public long getErrors() { return errors; }
    public void setErrors(long errors) { this.errors = errors; }
    
    public double getAverageExecutionTime() { return averageExecutionTime; }
    public void setAverageExecutionTime(double averageExecutionTime) { this.averageExecutionTime = averageExecutionTime; }
    
    public LocalDateTime getLastExecution() { return lastExecution; }
    public void setLastExecution(LocalDateTime lastExecution) { this.lastExecution = lastExecution; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}