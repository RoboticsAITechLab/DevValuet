package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Fingerprint biometric setup configuration
 */
public class FingerprintSetup {
    private boolean enabled;
    private String sensor;
    private double qualityThreshold;
    private int maxFingers;
    private boolean templateStorage;
    private Map<String, Object> parameters;
    
    // Constructors
    public FingerprintSetup() {}
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getSensor() { return sensor; }
    public void setSensor(String sensor) { this.sensor = sensor; }
    
    public double getQualityThreshold() { return qualityThreshold; }
    public void setQualityThreshold(double qualityThreshold) { this.qualityThreshold = qualityThreshold; }
    
    public int getMaxFingers() { return maxFingers; }
    public void setMaxFingers(int maxFingers) { this.maxFingers = maxFingers; }
    
    public boolean isTemplateStorage() { return templateStorage; }
    public void setTemplateStorage(boolean templateStorage) { this.templateStorage = templateStorage; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    // Additional methods needed by service
    public boolean isSupported() { return enabled; }
    public void setSupported(boolean supported) { this.enabled = supported; }
    
    public boolean isRequiresUserAction() { 
        return maxFingers == 0; // Requires action if no fingers registered
    }
    
    public void setRequiresUserAction(boolean requiresUserAction) {
        // This is a computed property, so we don't store it directly
        if (requiresUserAction && maxFingers > 0) {
            maxFingers = 0; // Reset to indicate user action needed
        }
    }
    
    public String getInstructions() { 
        return (String) (parameters != null ? parameters.get("instructions") : "Place your finger on the sensor for registration");
    }
    
    public void setInstructions(String instructions) {
        if (parameters == null) {
            parameters = new java.util.HashMap<>();
        }
        parameters.put("instructions", instructions);
    }
    
    public int getMinimumScans() {
        return (Integer) (parameters != null ? parameters.getOrDefault("minimumScans", 5) : 5);
    }
    
    public void setMinimumScans(int minimumScans) {
        if (parameters == null) {
            parameters = new java.util.HashMap<>();
        }
        parameters.put("minimumScans", minimumScans);
    }
}