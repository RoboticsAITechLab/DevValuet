package com.devvault.cockpit.model;

import java.util.Map;
import java.util.List;

/**
 * Workflow configuration and automation settings
 */
public class WorkflowConfiguration {
    private List<String> workflows;
    private Map<String, Boolean> enabledWorkflows;
    private Map<String, String> workflowParameters;
    private boolean autoExecution;
    private Map<String, Object> customWorkflows;
    
    // Constructors
    public WorkflowConfiguration() {}
    
    // Getters and Setters
    public List<String> getWorkflows() { return workflows; }
    public void setWorkflows(List<String> workflows) { this.workflows = workflows; }
    
    public Map<String, Boolean> getEnabledWorkflows() { return enabledWorkflows; }
    public void setEnabledWorkflows(Map<String, Boolean> enabledWorkflows) { this.enabledWorkflows = enabledWorkflows; }
    
    public Map<String, String> getWorkflowParameters() { return workflowParameters; }
    public void setWorkflowParameters(Map<String, String> workflowParameters) { this.workflowParameters = workflowParameters; }
    
    public boolean isAutoExecution() { return autoExecution; }
    public void setAutoExecution(boolean autoExecution) { this.autoExecution = autoExecution; }
    
    public Map<String, Object> getCustomWorkflows() { return customWorkflows; }
    public void setCustomWorkflows(Map<String, Object> customWorkflows) { this.customWorkflows = customWorkflows; }
}