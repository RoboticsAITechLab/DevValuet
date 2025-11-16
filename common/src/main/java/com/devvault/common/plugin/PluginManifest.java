package com.devvault.common.plugin;

/**
 * Minimal plugin manifest placeholder.
 * Real manifests should be signed and validated in production.
 */
public class PluginManifest {
    private String id;
    private String name;
    private String version;
    private boolean enabled = false;

    public PluginManifest() {}

    public PluginManifest(String id, String name, String version) {
        this.id = id;
        this.name = name;
        this.version = version;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
