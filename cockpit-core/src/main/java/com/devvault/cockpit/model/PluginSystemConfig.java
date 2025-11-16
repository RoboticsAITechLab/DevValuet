package com.devvault.cockpit.model;

import java.util.List;
import java.util.Map;

/**
 * Plugin system configuration and management
 */
public class PluginSystemConfig {
    private List<String> pluginDirectories;
    private boolean autoLoadPlugins;
    private Map<String, String> pluginSettings;
    private boolean sandboxMode;
    private Map<String, Object> pluginPermissions;
    
    // Constructors
    public PluginSystemConfig() {}
    
    // Getters and Setters
    public List<String> getPluginDirectories() { return pluginDirectories; }
    public void setPluginDirectories(List<String> pluginDirectories) { this.pluginDirectories = pluginDirectories; }
    
    public boolean isAutoLoadPlugins() { return autoLoadPlugins; }
    public void setAutoLoadPlugins(boolean autoLoadPlugins) { this.autoLoadPlugins = autoLoadPlugins; }
    
    public Map<String, String> getPluginSettings() { return pluginSettings; }
    public void setPluginSettings(Map<String, String> pluginSettings) { this.pluginSettings = pluginSettings; }
    
    public boolean isSandboxMode() { return sandboxMode; }
    public void setSandboxMode(boolean sandboxMode) { this.sandboxMode = sandboxMode; }
    
    public Map<String, Object> getPluginPermissions() { return pluginPermissions; }
    public void setPluginPermissions(Map<String, Object> pluginPermissions) { this.pluginPermissions = pluginPermissions; }
}