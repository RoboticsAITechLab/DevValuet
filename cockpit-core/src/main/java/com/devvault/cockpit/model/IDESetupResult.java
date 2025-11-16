package com.devvault.cockpit.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * IDE integration setup result and configuration
 */
public class IDESetupResult {
    private String setupId;
    private LocalDateTime setupTime;
    private boolean successful;
    private List<String> configuredIDEs;
    private PluginSetup pluginSetup;
    private DevEnvironmentConfig devEnvironment;
    private String errorMessage;
    private Map<String, Object> configurationData;
    
    // Constructors
    public IDESetupResult() {}
    
    // Getters and Setters
    public String getSetupId() { return setupId; }
    public void setSetupId(String setupId) { this.setupId = setupId; }
    
    public LocalDateTime getSetupTime() { return setupTime; }
    public void setSetupTime(LocalDateTime setupTime) { this.setupTime = setupTime; }
    
    public boolean isSuccessful() { return successful; }
    public void setSuccessful(boolean successful) { this.successful = successful; }
    
    public List<String> getConfiguredIDEs() { return configuredIDEs; }
    public void setConfiguredIDEs(List<String> configuredIDEs) { this.configuredIDEs = configuredIDEs; }
    
    public PluginSetup getPluginSetup() { return pluginSetup; }
    public void setPluginSetup(PluginSetup pluginSetup) { this.pluginSetup = pluginSetup; }
    
    public DevEnvironmentConfig getDevEnvironment() { return devEnvironment; }
    public void setDevEnvironment(DevEnvironmentConfig devEnvironment) { this.devEnvironment = devEnvironment; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public Map<String, Object> getConfigurationData() { return configurationData; }
    public void setConfigurationData(Map<String, Object> configurationData) { this.configurationData = configurationData; }
    
    // Additional methods needed by service
    public boolean isSuccess() { return successful; }
    public void setSuccess(boolean success) { this.successful = success; }
    
    public void setDetectedIDEs(List<DetectedIDE> detectedIDEs) { 
        // Convert to string list for compatibility
        this.configuredIDEs = detectedIDEs.stream().map(ide -> ide.getName()).collect(java.util.stream.Collectors.toList());
    }
    
    public void setRecommendations(List<IDERecommendation> recommendations) { 
        // Store in configuration data
        if (this.configurationData == null) {
            this.configurationData = new java.util.HashMap<>();
        }
        this.configurationData.put("recommendations", recommendations);
    }
    
    public void setDevEnvironmentConfig(DevEnvironmentConfig config) { this.devEnvironment = config; }
}