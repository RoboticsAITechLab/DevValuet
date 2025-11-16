package com.devvault.cockpit.model;

import java.util.List;

/**
 * Multi-Factor Authentication setup configuration
 */
public class MFASetup {
    private boolean enabled;
    private List<String> authMethods;
    private String primaryMethod;
    private String backupMethod;
    private int timeWindow;
    private boolean rememberDevice;
    
    // Constructors
    public MFASetup() {}
    
    // Getters and Setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public List<String> getAuthMethods() { return authMethods; }
    public void setAuthMethods(List<String> authMethods) { this.authMethods = authMethods; }
    
    public String getPrimaryMethod() { return primaryMethod; }
    public void setPrimaryMethod(String primaryMethod) { this.primaryMethod = primaryMethod; }
    
    public String getBackupMethod() { return backupMethod; }
    public void setBackupMethod(String backupMethod) { this.backupMethod = backupMethod; }
    
    public int getTimeWindow() { return timeWindow; }
    public void setTimeWindow(int timeWindow) { this.timeWindow = timeWindow; }
    
    public boolean isRememberDevice() { return rememberDevice; }
    public void setRememberDevice(boolean rememberDevice) { this.rememberDevice = rememberDevice; }
}