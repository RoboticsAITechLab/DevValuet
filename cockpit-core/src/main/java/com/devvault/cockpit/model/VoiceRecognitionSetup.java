package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Voice recognition biometric setup configuration
 */
public class VoiceRecognitionSetup {
    private boolean enabled;
    private String language;
    private double accuracyThreshold;
    private boolean noiseReduction;
    private int phraseCount;
    private Map<String, Object> parameters;
    
    // Constructors
    public VoiceRecognitionSetup() {}
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public double getAccuracyThreshold() { return accuracyThreshold; }
    public void setAccuracyThreshold(double accuracyThreshold) { this.accuracyThreshold = accuracyThreshold; }
    
    public boolean isNoiseReduction() { return noiseReduction; }
    public void setNoiseReduction(boolean noiseReduction) { this.noiseReduction = noiseReduction; }
    
    public int getPhraseCount() { return phraseCount; }
    public void setPhraseCount(int phraseCount) { this.phraseCount = phraseCount; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}