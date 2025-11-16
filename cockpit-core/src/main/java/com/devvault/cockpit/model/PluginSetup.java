package com.devvault.cockpit.model;

import java.util.List;
import java.util.Map;

/**
 * Plugin setup and management configuration
 */
public class PluginSetup {
    private List<String> installedPlugins;
    private List<String> recommendedPlugins;
    private Map<String, String> pluginVersions;
    private boolean autoUpdate;
    private Map<String, Object> pluginConfigurations;
    
    // Constructors
    public PluginSetup() {}
    
    // Getters and Setters
    public List<String> getInstalledPlugins() { return installedPlugins; }
    public void setInstalledPlugins(List<String> installedPlugins) { this.installedPlugins = installedPlugins; }
    
    public List<String> getRecommendedPlugins() { return recommendedPlugins; }
    public void setRecommendedPlugins(List<String> recommendedPlugins) { this.recommendedPlugins = recommendedPlugins; }
    
    public Map<String, String> getPluginVersions() { return pluginVersions; }
    public void setPluginVersions(Map<String, String> pluginVersions) { this.pluginVersions = pluginVersions; }
    
    public boolean isAutoUpdate() { return autoUpdate; }
    public void setAutoUpdate(boolean autoUpdate) { this.autoUpdate = autoUpdate; }
    
    public Map<String, Object> getPluginConfigurations() { return pluginConfigurations; }
    public void setPluginConfigurations(Map<String, Object> pluginConfigurations) { this.pluginConfigurations = pluginConfigurations; }
}