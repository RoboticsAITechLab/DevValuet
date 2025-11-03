package com.devvault.plugins;

import java.util.ServiceLoader;

/**
 * Simple plugin manager skeleton using ServiceLoader for discovery.
 * Plugins should implement the Plugin interface and provide service file under META-INF/services.
 */
public class PluginManager {
    public void discoverAndLoad() {
        ServiceLoader<Plugin> loader = ServiceLoader.load(Plugin.class);
        loader.forEach(plugin -> {
            System.out.println("Discovered plugin: " + plugin.getName());
            plugin.initialize();
        });
    }
}
