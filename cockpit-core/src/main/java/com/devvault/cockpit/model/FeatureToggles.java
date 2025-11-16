package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Feature toggles for enabling/disabling application features
 */
public class FeatureToggles {
    private Map<String, Boolean> features;
    private boolean advancedMode;
    private boolean betaFeatures;
    private boolean debugMode;
    private Map<String, Object> featureSettings;
    
    // Constructors
    public FeatureToggles() {}
    
    // Getters and Setters
    public Map<String, Boolean> getFeatures() { return features; }
    public void setFeatures(Map<String, Boolean> features) { this.features = features; }
    
    public boolean isAdvancedMode() { return advancedMode; }
    public void setAdvancedMode(boolean advancedMode) { this.advancedMode = advancedMode; }
    
    public boolean isBetaFeatures() { return betaFeatures; }
    public void setBetaFeatures(boolean betaFeatures) { this.betaFeatures = betaFeatures; }
    
    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean debugMode) { this.debugMode = debugMode; }
    
    public Map<String, Object> getFeatureSettings() { return featureSettings; }
    public void setFeatureSettings(Map<String, Object> featureSettings) { this.featureSettings = featureSettings; }
}