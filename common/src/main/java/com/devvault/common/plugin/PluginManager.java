package com.devvault.common.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Tiny plugin runtime skeleton. Plugins are represented by a folder under the plugins directory
 * containing a manifest.json (not parsed here yet). This manager provides enable/disable and a
 * simple list operation. Real implementations should run plugins in a sandbox and verify manifests.
 */
public class PluginManager {
    private final Path pluginsDir;

    public PluginManager(Path baseDir) throws Exception {
        this.pluginsDir = baseDir.resolve("plugins");
        if (!Files.exists(this.pluginsDir)) Files.createDirectories(this.pluginsDir);
    }

    public List<String> listPlugins() {
        try {
            List<String> out = new ArrayList<>();
            try (var s = Files.list(pluginsDir)) {
                s.filter(Files::isDirectory).forEach(p -> out.add(p.getFileName().toString()));
            }
            return out;
        } catch (Exception e) { return List.of(); }
    }

    public boolean enablePlugin(String id) {
        // placeholder: toggle a marker file
        try {
            Path p = pluginsDir.resolve(id);
            if (!Files.exists(p) || !Files.isDirectory(p)) return false;
            Files.writeString(p.resolve(".enabled"), "true");
            return true;
        } catch (Exception e) { return false; }
    }

    public boolean disablePlugin(String id) {
        try {
            Path p = pluginsDir.resolve(id);
            Path marker = p.resolve(".enabled");
            return Files.deleteIfExists(marker);
        } catch (Exception e) { return false; }
    }
}
