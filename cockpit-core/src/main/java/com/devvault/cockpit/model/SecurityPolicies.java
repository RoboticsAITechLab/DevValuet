package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Security policies configuration and enforcement
 */
public class SecurityPolicies {
    private String passwordPolicy;
    private int sessionTimeout;
    private boolean auditLogging;
    private String accessLevel;
    private Map<String, String> complianceSettings;
    private boolean encryptionRequired;
    
    // Constructors
    public SecurityPolicies() {}
    
    // Getters and Setters
    public String getPasswordPolicy() { return passwordPolicy; }
    public void setPasswordPolicy(String passwordPolicy) { this.passwordPolicy = passwordPolicy; }
    
    public int getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(int sessionTimeout) { this.sessionTimeout = sessionTimeout; }
    
    public boolean isAuditLogging() { return auditLogging; }
    public void setAuditLogging(boolean auditLogging) { this.auditLogging = auditLogging; }
    
    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    
    public Map<String, String> getComplianceSettings() { return complianceSettings; }
    public void setComplianceSettings(Map<String, String> complianceSettings) { this.complianceSettings = complianceSettings; }
    
    public boolean isEncryptionRequired() { return encryptionRequired; }
    public void setEncryptionRequired(boolean encryptionRequired) { this.encryptionRequired = encryptionRequired; }
}