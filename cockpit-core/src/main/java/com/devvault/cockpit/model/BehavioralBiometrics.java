package com.devvault.cockpit.model;

import java.util.Map;
import java.util.List;

/**
 * Behavioral biometrics analysis and setup
 */
public class BehavioralBiometrics {
    private boolean enabled;
    private List<String> analysisTypes;
    private Map<String, Double> thresholds;
    private boolean continuousMonitoring;
    private int learningPeriod;
    private Map<String, Object> parameters;
    
    // Constructors
    public BehavioralBiometrics() {}
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<String> getAnalysisTypes() { return analysisTypes; }
    public void setAnalysisTypes(List<String> analysisTypes) { this.analysisTypes = analysisTypes; }
    
    public Map<String, Double> getThresholds() { return thresholds; }
    public void setThresholds(Map<String, Double> thresholds) { this.thresholds = thresholds; }
    
    public boolean isContinuousMonitoring() { return continuousMonitoring; }
    public void setContinuousMonitoring(boolean continuousMonitoring) { this.continuousMonitoring = continuousMonitoring; }
    
    public int getLearningPeriod() { return learningPeriod; }
    public void setLearningPeriod(int learningPeriod) { this.learningPeriod = learningPeriod; }
    
    public Map<String, Object> getParameters() { return parameters; }
    public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
}