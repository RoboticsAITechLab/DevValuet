package com.devvault.cockpit.model;

import java.util.Map;

/**
 * System optimization recommendation
 */
public class SystemOptimization {
    private String optimizationType;
    private String description;
    private String priority;
    private Map<String, Object> parameters;
    private boolean autoApply;
    
    // Constructors
    public SystemOptimization() {}
    
    // Getters and Setters
    public String getOptimizationType() { return optimizationType; }
    public void setOptimizationType(String optimizationType) { this.optimizationType = optimizationType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public boolean isAutoApply() { return autoApply; }
    public void setAutoApply(boolean autoApply) { this.autoApply = autoApply; }
}