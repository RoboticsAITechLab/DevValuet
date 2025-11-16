package com.devvault.cockpit.model;

/**
 * Security recommendation for enhanced security posture
 */
public class SecurityRecommendation {
    private String recommendationType;
    private String description;
    private String severity;
    private String implementation;
    private boolean critical;
    
    // Constructors
    public SecurityRecommendation() {}
    
    // Getters and Setters
    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getImplementation() { return implementation; }
    public void setImplementation(String implementation) { this.implementation = implementation; }
    
    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }
}