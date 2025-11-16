package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Performance tuning recommendation
 */
public class PerformanceTuning {
    private String tuningType;
    private String description;
    private Map<String, Object> parameters;
    private double expectedImprovement;
    private String impact;
    
    // Constructors
    public PerformanceTuning() {}
    
    // Getters and Setters
    public String getTuningType() { return tuningType; }
    public void setTuningType(String tuningType) { this.tuningType = tuningType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    public double getExpectedImprovement() { return expectedImprovement; }
    public void setExpectedImprovement(double expectedImprovement) { this.expectedImprovement = expectedImprovement; }
    
    public String getImpact() { return impact; }
    public void setImpact(String impact) { this.impact = impact; }
}