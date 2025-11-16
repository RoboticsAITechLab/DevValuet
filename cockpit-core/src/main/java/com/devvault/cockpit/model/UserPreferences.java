package com.devvault.cockpit.model;

import java.util.List;
import java.util.Map;

/**
 * User preferences for the setup process
 */
public class UserPreferences {
    private String experienceLevel; // Beginner, Intermediate, Advanced, Expert
    private List<String> primaryUseCases; // Development, Security, Backup, etc.
    private List<String> preferredLanguages; // Java, Python, JavaScript, etc.
    private String setupMode; // Quick, Detailed, Custom
    private boolean enableTelemetry;
    private boolean enableAutomaticUpdates;
    private boolean enableBetaFeatures;
    private String preferredTheme;
    private Map<String, Object> customPreferences;
    
    // Constructors
    public UserPreferences() {}
    
    // Getters and Setters
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    
    public List<String> getPrimaryUseCases() { return primaryUseCases; }
    public void setPrimaryUseCases(List<String> primaryUseCases) { this.primaryUseCases = primaryUseCases; }
    
    public List<String> getPreferredLanguages() { return preferredLanguages; }
    public void setPreferredLanguages(List<String> preferredLanguages) { this.preferredLanguages = preferredLanguages; }
    
    public String getSetupMode() { return setupMode; }
    public void setSetupMode(String setupMode) { this.setupMode = setupMode; }
    
    public boolean isEnableTelemetry() { return enableTelemetry; }
    public void setEnableTelemetry(boolean enableTelemetry) { this.enableTelemetry = enableTelemetry; }
    
    public boolean isEnableAutomaticUpdates() { return enableAutomaticUpdates; }
    public void setEnableAutomaticUpdates(boolean enableAutomaticUpdates) { this.enableAutomaticUpdates = enableAutomaticUpdates; }
    
    public boolean isEnableBetaFeatures() { return enableBetaFeatures; }
    public void setEnableBetaFeatures(boolean enableBetaFeatures) { this.enableBetaFeatures = enableBetaFeatures; }
    
    public String getPreferredTheme() { return preferredTheme; }
    public void setPreferredTheme(String preferredTheme) { this.preferredTheme = preferredTheme; }
    
    public Map<String, Object> getCustomPreferences() { return customPreferences; }
    public void setCustomPreferences(Map<String, Object> customPreferences) { this.customPreferences = customPreferences; }
}