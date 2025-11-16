package com.devvault.cockpit.model;

import java.util.List;

/**
 * AI-powered recommendations for system optimization
 */
public class AIRecommendations {
    private List<SystemOptimization> systemOptimizations;
    private List<SecurityRecommendation> securityRecommendations;
    private List<PerformanceTuning> performanceTuning;
    private List<WorkflowSuggestion> workflowSuggestions;
    private List<CustomizationSuggestion> customizationSuggestions;
    
    // Constructors
    public AIRecommendations() {}
    
    // Getters and Setters
    public List<SystemOptimization> getSystemOptimizations() { return systemOptimizations; }
    public void setSystemOptimizations(List<SystemOptimization> systemOptimizations) { this.systemOptimizations = systemOptimizations; }
    
    public List<SecurityRecommendation> getSecurityRecommendations() { return securityRecommendations; }
    public void setSecurityRecommendations(List<SecurityRecommendation> securityRecommendations) { this.securityRecommendations = securityRecommendations; }
    
    public List<PerformanceTuning> getPerformanceTuning() { return performanceTuning; }
    public void setPerformanceTuning(List<PerformanceTuning> performanceTuning) { this.performanceTuning = performanceTuning; }
    
    public List<WorkflowSuggestion> getWorkflowSuggestions() { return workflowSuggestions; }
    public void setWorkflowSuggestions(List<WorkflowSuggestion> workflowSuggestions) { this.workflowSuggestions = workflowSuggestions; }
    
    public List<CustomizationSuggestion> getCustomizationSuggestions() { return customizationSuggestions; }
    public void setCustomizationSuggestions(List<CustomizationSuggestion> customizationSuggestions) { this.customizationSuggestions = customizationSuggestions; }
}