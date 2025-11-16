package com.devvault.frontend.services;

import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.http.HttpClientConfig;
import com.devvault.frontend.dto.PluginDto;
import com.devvault.frontend.dto.PluginConfigDto;
import com.devvault.frontend.dto.PluginMetricsDto;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise Plugin Service
 * Handles plugin management, installation, and lifecycle operations
 */
public class PluginService {
    
    private static final Logger logger = LoggerFactory.getLogger(PluginService.class);
    
    private final HttpClientService httpClient;
    private final ObservableList<PluginDto> installedPlugins;
    private final ObservableList<PluginDto> availablePlugins;
    
    // Plugin Status Properties
    private final IntegerProperty totalInstalledPlugins = new SimpleIntegerProperty(0);
    private final IntegerProperty activePlugins = new SimpleIntegerProperty(0);
    private final BooleanProperty autoUpdateEnabled = new SimpleBooleanProperty(true);
    private final StringProperty pluginDirectory = new SimpleStringProperty("");
    
    private static PluginService instance;
    
    private PluginService() {
        this.httpClient = HttpClientService.getInstance();
        this.installedPlugins = FXCollections.observableArrayList();
        this.availablePlugins = FXCollections.observableArrayList();
        logger.info("Enterprise PluginService initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized PluginService getInstance() {
        if (instance == null) {
            instance = new PluginService();
        }
        return instance;
    }
    
    // ============================================
    // PLUGIN DISCOVERY & INSTALLATION
    // ============================================
    
    /**
     * Get all installed plugins
     */
    public ObservableList<PluginDto> getInstalledPlugins() {
        return installedPlugins;
    }
    
    /**
     * Get available plugins from marketplace
     */
    public ObservableList<PluginDto> getAvailablePlugins() {
        return availablePlugins;
    }
    
    /**
     * Refresh installed plugins
     */
    public CompletableFuture<Void> refreshInstalledPlugins() {
        return httpClient.getCockpitCoreAsync("/api/plugins/installed", new TypeReference<List<PluginDto>>() {})
                .thenAccept(pluginList -> {
                    javafx.application.Platform.runLater(() -> {
                        installedPlugins.clear();
                        installedPlugins.addAll(pluginList);
                        totalInstalledPlugins.set(pluginList.size());
                        activePlugins.set((int) pluginList.stream().filter(PluginDto::isEnabled).count());
                    });
                    logger.info("Refreshed {} installed plugins", pluginList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh installed plugins", throwable);
                    return null;
                });
    }
    
    /**
     * Refresh available plugins from marketplace
     */
    public CompletableFuture<Void> refreshAvailablePlugins() {
        return httpClient.getCockpitCoreAsync("/api/plugins/marketplace", new TypeReference<List<PluginDto>>() {})
                .thenAccept(pluginList -> {
                    javafx.application.Platform.runLater(() -> {
                        availablePlugins.clear();
                        availablePlugins.addAll(pluginList);
                    });
                    logger.info("Refreshed {} available plugins from marketplace", pluginList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh available plugins", throwable);
                    return null;
                });
    }
    
    /**
     * Install plugin
     */
    public CompletableFuture<PluginDto> installPlugin(String pluginId, String version) {
        logger.info("Installing plugin: {} version {}", pluginId, version);
        
        PluginInstallRequest request = new PluginInstallRequest();
        request.setPluginId(pluginId);
        request.setVersion(version);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/install", request, PluginDto.class)
                .thenApply(plugin -> {
                    javafx.application.Platform.runLater(() -> {
                        installedPlugins.add(plugin);
                        totalInstalledPlugins.set(installedPlugins.size());
                        if (plugin.isEnabled()) {
                            activePlugins.set(activePlugins.get() + 1);
                        }
                    });
                    logger.info("Plugin installed successfully: {}", plugin.getName());
                    return plugin;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin installation failed: {}", pluginId, throwable);
                    throw new RuntimeException("Plugin installation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Install plugin from file
     */
    public CompletableFuture<PluginDto> installPluginFromFile(String filePath, boolean validateSignature) {
        logger.info("Installing plugin from file: {} (validate: {})", filePath, validateSignature);
        
        PluginInstallFromFileRequest request = new PluginInstallFromFileRequest();
        request.setFilePath(filePath);
        request.setValidateSignature(validateSignature);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/install-from-file", request, PluginDto.class)
                .thenApply(plugin -> {
                    javafx.application.Platform.runLater(() -> {
                        installedPlugins.add(plugin);
                        totalInstalledPlugins.set(installedPlugins.size());
                        if (plugin.isEnabled()) {
                            activePlugins.set(activePlugins.get() + 1);
                        }
                    });
                    logger.info("Plugin installed from file successfully: {}", plugin.getName());
                    return plugin;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin installation from file failed: {}", filePath, throwable);
                    throw new RuntimeException("Plugin installation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Uninstall plugin
     */
    public CompletableFuture<Boolean> uninstallPlugin(String pluginId, boolean removeData) {
        logger.info("Uninstalling plugin: {} (removeData: {})", pluginId, removeData);
        
        String endpoint = "/api/plugins/" + pluginId + "/uninstall?removeData=" + removeData;
        
        return httpClient.deleteAsync(endpoint, HttpClientConfig.COCKPIT_CORE_BASE_URL)
                .thenApply(result -> {
                    javafx.application.Platform.runLater(() -> {
                        PluginDto removedPlugin = installedPlugins.stream()
                                .filter(plugin -> plugin.getId().equals(pluginId))
                                .findFirst()
                                .orElse(null);
                        
                        if (removedPlugin != null) {
                            installedPlugins.remove(removedPlugin);
                            totalInstalledPlugins.set(installedPlugins.size());
                            if (removedPlugin.isEnabled()) {
                                activePlugins.set(activePlugins.get() - 1);
                            }
                        }
                    });
                    logger.info("Plugin uninstalled successfully: {}", pluginId);
                    return true;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin uninstallation failed: {}", pluginId, throwable);
                    return false;
                });
    }
    
    // ============================================
    // PLUGIN LIFECYCLE MANAGEMENT
    // ============================================
    
    /**
     * Enable plugin
     */
    public CompletableFuture<Boolean> enablePlugin(String pluginId) {
        logger.info("Enabling plugin: {}", pluginId);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/" + pluginId + "/enable", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            installedPlugins.stream()
                                    .filter(plugin -> plugin.getId().equals(pluginId))
                                    .findFirst()
                                    .ifPresent(plugin -> {
                                        plugin.setEnabled(true);
                                        activePlugins.set(activePlugins.get() + 1);
                                    });
                        });
                        logger.info("Plugin enabled successfully: {}", pluginId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin enable failed: {}", pluginId, throwable);
                    return false;
                });
    }
    
    /**
     * Disable plugin
     */
    public CompletableFuture<Boolean> disablePlugin(String pluginId) {
        logger.info("Disabling plugin: {}", pluginId);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/" + pluginId + "/disable", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            installedPlugins.stream()
                                    .filter(plugin -> plugin.getId().equals(pluginId))
                                    .findFirst()
                                    .ifPresent(plugin -> {
                                        plugin.setEnabled(false);
                                        activePlugins.set(activePlugins.get() - 1);
                                    });
                        });
                        logger.info("Plugin disabled successfully: {}", pluginId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin disable failed: {}", pluginId, throwable);
                    return false;
                });
    }
    
    /**
     * Restart plugin
     */
    public CompletableFuture<Boolean> restartPlugin(String pluginId) {
        logger.info("Restarting plugin: {}", pluginId);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/" + pluginId + "/restart", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Plugin restarted successfully: {}", pluginId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin restart failed: {}", pluginId, throwable);
                    return false;
                });
    }
    
    /**
     * Update plugin
     */
    public CompletableFuture<PluginDto> updatePlugin(String pluginId, String targetVersion) {
        logger.info("Updating plugin: {} to version {}", pluginId, targetVersion);
        
        PluginUpdateRequest request = new PluginUpdateRequest();
        request.setPluginId(pluginId);
        request.setTargetVersion(targetVersion);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/update", request, PluginDto.class)
                .thenApply(updatedPlugin -> {
                    javafx.application.Platform.runLater(() -> {
                        // Update plugin in list
                        for (int i = 0; i < installedPlugins.size(); i++) {
                            if (installedPlugins.get(i).getId().equals(pluginId)) {
                                installedPlugins.set(i, updatedPlugin);
                                break;
                            }
                        }
                    });
                    logger.info("Plugin updated successfully: {} -> {}", pluginId, targetVersion);
                    return updatedPlugin;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin update failed: {}", pluginId, throwable);
                    throw new RuntimeException("Plugin update failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Check for plugin updates
     */
    public CompletableFuture<List<PluginUpdateInfoDto>> checkForUpdates() {
        logger.info("Checking for plugin updates");
        
        return httpClient.getCockpitCoreAsync("/api/plugins/check-updates", new TypeReference<List<PluginUpdateInfoDto>>() {})
                .thenApply(updates -> {
                    logger.info("Found {} plugin updates available", updates.size());
                    return updates;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to check for plugin updates", throwable);
                    return List.of();
                });
    }
    
    // ============================================
    // PLUGIN CONFIGURATION
    // ============================================
    
    /**
     * Get plugin configuration
     */
    public CompletableFuture<PluginConfigDto> getPluginConfig(String pluginId) {
        return httpClient.getCockpitCoreAsync("/api/plugins/" + pluginId + "/config", PluginConfigDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get plugin config: {}", pluginId, throwable);
                    return new PluginConfigDto();
                });
    }
    
    /**
     * Update plugin configuration
     */
    public CompletableFuture<Boolean> updatePluginConfig(String pluginId, Map<String, Object> config) {
        logger.info("Updating configuration for plugin: {}", pluginId);
        
        PluginConfigUpdateRequest request = new PluginConfigUpdateRequest();
        request.setPluginId(pluginId);
        request.setConfig(config);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/" + pluginId + "/config", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Plugin configuration updated successfully: {}", pluginId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin configuration update failed: {}", pluginId, throwable);
                    return false;
                });
    }
    
    // ============================================
    // PLUGIN MONITORING
    // ============================================
    
    /**
     * Get plugin metrics
     */
    public CompletableFuture<PluginMetricsDto> getPluginMetrics(String pluginId) {
        return httpClient.getCockpitCoreAsync("/api/plugins/" + pluginId + "/metrics", PluginMetricsDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get plugin metrics: {}", pluginId, throwable);
                    return new PluginMetricsDto();
                });
    }
    
    /**
     * Get plugin logs
     */
    public CompletableFuture<List<String>> getPluginLogs(String pluginId, int lines) {
        return httpClient.getCockpitCoreAsync("/api/plugins/" + pluginId + "/logs?lines=" + lines, 
                new TypeReference<List<String>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get plugin logs: {}", pluginId, throwable);
                    return List.of();
                });
    }
    
    /**
     * Get plugin health status
     */
    public CompletableFuture<PluginHealthDto> getPluginHealth(String pluginId) {
        return httpClient.getCockpitCoreAsync("/api/plugins/" + pluginId + "/health", PluginHealthDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get plugin health: {}", pluginId, throwable);
                    return new PluginHealthDto();
                });
    }
    
    // ============================================
    // MARKETPLACE OPERATIONS
    // ============================================
    
    /**
     * Search plugins in marketplace
     */
    public CompletableFuture<List<PluginDto>> searchMarketplace(String query, String category, String sortBy) {
        String endpoint = String.format("/api/plugins/marketplace/search?q=%s&category=%s&sort=%s", 
                query, category, sortBy);
        
        return httpClient.getCockpitCoreAsync(endpoint, new TypeReference<List<PluginDto>>() {})
                .thenApply(results -> {
                    logger.info("Found {} plugins matching search: {}", results.size(), query);
                    return results;
                })
                .exceptionally(throwable -> {
                    logger.error("Marketplace search failed: {}", query, throwable);
                    return List.of();
                });
    }
    
    /**
     * Get plugin details from marketplace
     */
    public CompletableFuture<PluginDto> getMarketplacePluginDetails(String pluginId) {
        return httpClient.getCockpitCoreAsync("/api/plugins/marketplace/" + pluginId, PluginDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get marketplace plugin details: {}", pluginId, throwable);
                    return null;
                });
    }
    
    /**
     * Submit plugin to marketplace
     */
    public CompletableFuture<Boolean> submitToMarketplace(String pluginPath, PluginSubmissionDto submission) {
        logger.info("Submitting plugin to marketplace: {}", submission.getName());
        
        PluginSubmissionRequest request = new PluginSubmissionRequest();
        request.setPluginPath(pluginPath);
        request.setSubmission(submission);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/marketplace/submit", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Plugin submitted to marketplace successfully: {}", submission.getName());
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Plugin marketplace submission failed: {}", submission.getName(), throwable);
                    return false;
                });
    }
    
    // ============================================
    // AUTO-UPDATE MANAGEMENT
    // ============================================
    
    /**
     * Enable auto-update for plugin
     */
    public CompletableFuture<Boolean> enableAutoUpdate(String pluginId) {
        logger.info("Enabling auto-update for plugin: {}", pluginId);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/" + pluginId + "/auto-update/enable", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Auto-update enabled for plugin: {}", pluginId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to enable auto-update for plugin: {}", pluginId, throwable);
                    return false;
                });
    }
    
    /**
     * Disable auto-update for plugin
     */
    public CompletableFuture<Boolean> disableAutoUpdate(String pluginId) {
        logger.info("Disabling auto-update for plugin: {}", pluginId);
        
        return httpClient.postCockpitCoreAsync("/api/plugins/" + pluginId + "/auto-update/disable", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Auto-update disabled for plugin: {}", pluginId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to disable auto-update for plugin: {}", pluginId, throwable);
                    return false;
                });
    }
    
    /**
     * Run auto-update check for all plugins
     */
    public CompletableFuture<Integer> runAutoUpdateCheck() {
        logger.info("Running auto-update check for all plugins");
        
        return httpClient.postCockpitCoreAsync("/api/plugins/auto-update/check", null, Integer.class)
                .thenApply(updatesInstalled -> {
                    logger.info("Auto-update completed - {} plugins updated", updatesInstalled);
                    if (updatesInstalled > 0) {
                        // Refresh plugin list after updates
                        refreshInstalledPlugins();
                    }
                    return updatesInstalled;
                })
                .exceptionally(throwable -> {
                    logger.error("Auto-update check failed", throwable);
                    return 0;
                });
    }
    
    // ============================================
    // PROPERTY ACCESSORS
    // ============================================
    
    public IntegerProperty totalInstalledPluginsProperty() { return totalInstalledPlugins; }
    public int getTotalInstalledPlugins() { return totalInstalledPlugins.get(); }
    
    public IntegerProperty activePluginsProperty() { return activePlugins; }
    public int getActivePlugins() { return activePlugins.get(); }
    
    public BooleanProperty autoUpdateEnabledProperty() { return autoUpdateEnabled; }
    public boolean isAutoUpdateEnabled() { return autoUpdateEnabled.get(); }
    public void setAutoUpdateEnabled(boolean enabled) { autoUpdateEnabled.set(enabled); }
    
    public StringProperty pluginDirectoryProperty() { return pluginDirectory; }
    public String getPluginDirectory() { return pluginDirectory.get(); }
    public void setPluginDirectory(String directory) { pluginDirectory.set(directory); }
    
    // ============================================
    // DTO CLASSES
    // ============================================
    
    private static class PluginInstallRequest {
        private String pluginId;
        private String version;
        
        public String getPluginId() { return pluginId; }
        public void setPluginId(String pluginId) { this.pluginId = pluginId; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }
    
    private static class PluginInstallFromFileRequest {
        private String filePath;
        private boolean validateSignature;
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public boolean isValidateSignature() { return validateSignature; }
        public void setValidateSignature(boolean validateSignature) { this.validateSignature = validateSignature; }
    }
    
    private static class PluginUpdateRequest {
        private String pluginId;
        private String targetVersion;
        
        public String getPluginId() { return pluginId; }
        public void setPluginId(String pluginId) { this.pluginId = pluginId; }
        
        public String getTargetVersion() { return targetVersion; }
        public void setTargetVersion(String targetVersion) { this.targetVersion = targetVersion; }
    }
    
    private static class PluginConfigUpdateRequest {
        private String pluginId;
        private Map<String, Object> config;
        
        public String getPluginId() { return pluginId; }
        public void setPluginId(String pluginId) { this.pluginId = pluginId; }
        
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
    
    private static class PluginSubmissionRequest {
        private String pluginPath;
        private PluginSubmissionDto submission;
        
        public String getPluginPath() { return pluginPath; }
        public void setPluginPath(String pluginPath) { this.pluginPath = pluginPath; }
        
        public PluginSubmissionDto getSubmission() { return submission; }
        public void setSubmission(PluginSubmissionDto submission) { this.submission = submission; }
    }
    
    public static class PluginUpdateInfoDto {
        private String pluginId;
        private String currentVersion;
        private String latestVersion;
        private String releaseNotes;
        private boolean criticalUpdate;
        
        public String getPluginId() { return pluginId; }
        public void setPluginId(String pluginId) { this.pluginId = pluginId; }
        
        public String getCurrentVersion() { return currentVersion; }
        public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }
        
        public String getLatestVersion() { return latestVersion; }
        public void setLatestVersion(String latestVersion) { this.latestVersion = latestVersion; }
        
        public String getReleaseNotes() { return releaseNotes; }
        public void setReleaseNotes(String releaseNotes) { this.releaseNotes = releaseNotes; }
        
        public boolean isCriticalUpdate() { return criticalUpdate; }
        public void setCriticalUpdate(boolean criticalUpdate) { this.criticalUpdate = criticalUpdate; }
    }
    
    public static class PluginHealthDto {
        private String status;
        private String message;
        private long lastCheck;
        private Map<String, Object> details;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getLastCheck() { return lastCheck; }
        public void setLastCheck(long lastCheck) { this.lastCheck = lastCheck; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
    
    public static class PluginSubmissionDto {
        private String name;
        private String description;
        private String category;
        private List<String> tags;
        private String iconPath;
        private List<String> screenshots;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        
        public String getIconPath() { return iconPath; }
        public void setIconPath(String iconPath) { this.iconPath = iconPath; }
        
        public List<String> getScreenshots() { return screenshots; }
        public void setScreenshots(List<String> screenshots) { this.screenshots = screenshots; }
    }
}