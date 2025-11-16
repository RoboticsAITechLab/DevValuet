package com.devvault.cockpit.model;

/**
 * Comprehensive customization setup configuration
 */
public class CustomizationSetup {
    private boolean success;
    private String errorMessage;
    private ThemeConfiguration themeConfiguration;
    private LayoutConfiguration layoutConfiguration;
    private WorkflowConfiguration workflowConfiguration;
    private FeatureToggles featureToggles;
    private PluginSystemConfig pluginSystemConfig;
    private PerformanceSettings performanceSettings;
    
    // Constructors
    public CustomizationSetup() {}
    
    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public ThemeConfiguration getThemeConfiguration() { return themeConfiguration; }
    public void setThemeConfiguration(ThemeConfiguration themeConfiguration) { this.themeConfiguration = themeConfiguration; }
    
    public LayoutConfiguration getLayoutConfiguration() { return layoutConfiguration; }
    public void setLayoutConfiguration(LayoutConfiguration layoutConfiguration) { this.layoutConfiguration = layoutConfiguration; }
    
    public WorkflowConfiguration getWorkflowConfiguration() { return workflowConfiguration; }
    public void setWorkflowConfiguration(WorkflowConfiguration workflowConfiguration) { this.workflowConfiguration = workflowConfiguration; }
    
    public FeatureToggles getFeatureToggles() { return featureToggles; }
    public void setFeatureToggles(FeatureToggles featureToggles) { this.featureToggles = featureToggles; }
    
    public PluginSystemConfig getPluginSystemConfig() { return pluginSystemConfig; }
    public void setPluginSystemConfig(PluginSystemConfig pluginSystemConfig) { this.pluginSystemConfig = pluginSystemConfig; }
    
    public PerformanceSettings getPerformanceSettings() { return performanceSettings; }
    public void setPerformanceSettings(PerformanceSettings performanceSettings) { this.performanceSettings = performanceSettings; }
}