package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Face recognition biometric setup configuration
 */
public class FaceRecognitionSetup {
    private boolean enabled;
    private String algorithm;
    private double confidenceThreshold;
    private boolean liveDetection;
    private int enrollmentCount;
    private Map<String, Object> parameters;
    
    // Constructors
    public FaceRecognitionSetup() {}
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public double getConfidenceThreshold() { return confidenceThreshold; }
    public void setConfidenceThreshold(double confidenceThreshold) { this.confidenceThreshold = confidenceThreshold; }
    
    public boolean isLiveDetection() { return liveDetection; }
    public void setLiveDetection(boolean liveDetection) { this.liveDetection = liveDetection; }
    
    public int getEnrollmentCount() { return enrollmentCount; }
    public void setEnrollmentCount(int enrollmentCount) { this.enrollmentCount = enrollmentCount; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    
    // Additional methods needed by service
    public boolean isSupported() { return enabled; }
    public void setSupported(boolean supported) { this.enabled = supported; }
    
    public boolean isRequiresUserAction() { 
        return enrollmentCount == 0; // Requires action if not yet enrolled
    }
    
    public void setRequiresUserAction(boolean requiresUserAction) {
        // This is a computed property, so we don't store it directly
        if (requiresUserAction && enrollmentCount > 0) {
            enrollmentCount = 0; // Reset enrollment to indicate user action needed
        }
    }
    
    public String getInstructions() { 
        return (String) (parameters != null ? parameters.get("instructions") : "Look directly at the camera for face registration");
    }
    
    public void setInstructions(String instructions) {
        if (parameters == null) {
            parameters = new java.util.HashMap<>();
        }
        parameters.put("instructions", instructions);
    }
    
    public int getMinimumSamples() {
        return (Integer) (parameters != null ? parameters.getOrDefault("minimumSamples", 3) : 3);
    }
    
    public void setMinimumSamples(int minimumSamples) {
        if (parameters == null) {
            parameters = new java.util.HashMap<>();
        }
        parameters.put("minimumSamples", minimumSamples);
    }
    
    public double getQualityThreshold() { return confidenceThreshold; }
    public void setQualityThreshold(double qualityThreshold) { this.confidenceThreshold = qualityThreshold; }
}