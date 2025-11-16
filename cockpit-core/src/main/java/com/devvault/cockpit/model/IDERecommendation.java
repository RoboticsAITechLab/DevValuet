package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Smart IDE recommendation based on project analysis
 */
public class IDERecommendation {
    
    private String ideName;
    private double score; // 0.0 to 1.0
    private String reasoning;
    private UserConfiguredIDE suggestedConfiguration;
    private Map<String, String> recommendedSettings;
    private boolean isInstalled;
    private String installationUrl;
    
    // Constructors
    public IDERecommendation() {}
    
    public IDERecommendation(String ideName, double score, String reasoning) {
        this.ideName = ideName;
        this.score = score;
        this.reasoning = reasoning;
    }
    
    // Getters and setters
    public String getIdeName() { return ideName; }
    public void setIdeName(String ideName) { this.ideName = ideName; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    
    public UserConfiguredIDE getSuggestedConfiguration() { return suggestedConfiguration; }
    public void setSuggestedConfiguration(UserConfiguredIDE suggestedConfiguration) { 
        this.suggestedConfiguration = suggestedConfiguration; 
    }
    
    public Map<String, String> getRecommendedSettings() { return recommendedSettings; }
    public void setRecommendedSettings(Map<String, String> recommendedSettings) { 
        this.recommendedSettings = recommendedSettings; 
    }
    
    public boolean isInstalled() { return isInstalled; }
    public void setInstalled(boolean installed) { isInstalled = installed; }
    
    public String getInstallationUrl() { return installationUrl; }
    public void setInstallationUrl(String installationUrl) { this.installationUrl = installationUrl; }
    
    @Override
    public String toString() {
        return "IDERecommendation{" +
               "ideName='" + ideName + '\'' +
               ", score=" + score +
               ", reasoning='" + reasoning + '\'' +
               ", isInstalled=" + isInstalled +
               '}';
    }
}