package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Iris biometric setup configuration
 */
public class IrisSetup {
    private boolean enabled;
    private String scanner;
    private double recognitionThreshold;
    private boolean bilateral;
    private int captureQuality;
    private Map<String, Object> parameters;
    
    // Constructors
    public IrisSetup() {}
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getScanner() { return scanner; }
    public void setScanner(String scanner) { this.scanner = scanner; }
    
    public double getRecognitionThreshold() { return recognitionThreshold; }
    public void setRecognitionThreshold(double recognitionThreshold) { this.recognitionThreshold = recognitionThreshold; }
    
    public boolean isBilateral() { return bilateral; }
    public void setBilateral(boolean bilateral) { this.bilateral = bilateral; }
    
    public int getCaptureQuality() { return captureQuality; }
    public void setCaptureQuality(int captureQuality) { this.captureQuality = captureQuality; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}