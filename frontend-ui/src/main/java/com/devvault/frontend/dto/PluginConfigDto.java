package com.devvault.frontend.dto;

import java.util.Map;

/**
 * DTO for plugin configuration
 */
public class PluginConfigDto {
    private String id;
    private String pluginId;
    private String name;
    private String version;
    private boolean enabled;
    private Map<String, Object> settings;
    private String description;
    
    // Constructors
    public PluginConfigDto() {}
    
    public PluginConfigDto(String pluginId, String name, boolean enabled) {
        this.pluginId = pluginId;
        this.name = name;
        this.enabled = enabled;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getPluginId() { return pluginId; }
    public void setPluginId(String pluginId) { this.pluginId = pluginId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public Map<String, Object> getSettings() { return settings; }
    public void setSettings(Map<String, Object> settings) { this.settings = settings; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}