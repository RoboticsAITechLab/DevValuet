package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Secure storage configuration and management
 */
public class SecureStorageConfig {
    private String storageType;
    private String encryptionMethod;
    private String storageLocation;
    private boolean compression;
    private Map<String, String> accessControls;
    private boolean backupEnabled;
    
    // Constructors
    public SecureStorageConfig() {}
    
    // Getters and Setters
    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }
    
    public String getEncryptionMethod() { return encryptionMethod; }
    public void setEncryptionMethod(String encryptionMethod) { this.encryptionMethod = encryptionMethod; }
    
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    
    public boolean isCompression() { return compression; }
    public void setCompression(boolean compression) { this.compression = compression; }
    
    public Map<String, String> getAccessControls() { return accessControls; }
    public void setAccessControls(Map<String, String> accessControls) { this.accessControls = accessControls; }
    
    public boolean isBackupEnabled() { return backupEnabled; }
    public void setBackupEnabled(boolean backupEnabled) { this.backupEnabled = backupEnabled; }
}