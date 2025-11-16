package com.devvault.cockpit.model;

/**
 * Workflow optimization suggestion
 */
public class WorkflowSuggestion {
    private String workflowName;
    private String suggestion;
    private String category;
    private String benefit;
    private boolean implementable;
    
    // Constructors
    public WorkflowSuggestion() {}
    
    // Getters and Setters
    public String getWorkflowName() { return workflowName; }
    public void setWorkflowName(String workflowName) { this.workflowName = workflowName; }
    
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getBenefit() { return benefit; }
    public void setBenefit(String benefit) { this.benefit = benefit; }
    
    public boolean isImplementable() { return implementable; }
    public void setImplementable(boolean implementable) { this.implementable = implementable; }
}