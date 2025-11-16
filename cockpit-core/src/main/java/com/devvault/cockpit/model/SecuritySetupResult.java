package com.devvault.cockpit.model;

import java.time.LocalDateTime;

/**
 * Security setup result containing comprehensive security configuration results
 */
public class SecuritySetupResult {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
    private EncryptionKeys encryptionKeys;
    private CertificateSetup certificateSetup;
    private SecureStorageConfig secureStorageConfig;
    private MFASetup mfaSetup;
    private BiometricSetupResult biometricSetup;
    private SecurityPolicies securityPolicies;
    
    // Constructors
    public SecuritySetupResult() {
        this.timestamp = LocalDateTime.now();
    }
    
    public SecuritySetupResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public EncryptionKeys getEncryptionKeys() { return encryptionKeys; }
    public void setEncryptionKeys(EncryptionKeys encryptionKeys) { this.encryptionKeys = encryptionKeys; }
    
    public CertificateSetup getCertificateSetup() { return certificateSetup; }
    public void setCertificateSetup(CertificateSetup certificateSetup) { this.certificateSetup = certificateSetup; }
    
    public SecureStorageConfig getSecureStorageConfig() { return secureStorageConfig; }
    public void setSecureStorageConfig(SecureStorageConfig secureStorageConfig) { this.secureStorageConfig = secureStorageConfig; }
    
    public MFASetup getMfaSetup() { return mfaSetup; }
    public void setMfaSetup(MFASetup mfaSetup) { this.mfaSetup = mfaSetup; }
    
    public BiometricSetupResult getBiometricSetup() { return biometricSetup; }
    public void setBiometricSetup(BiometricSetupResult biometricSetup) { this.biometricSetup = biometricSetup; }
    
    public SecurityPolicies getSecurityPolicies() { return securityPolicies; }
    public void setSecurityPolicies(SecurityPolicies securityPolicies) { this.securityPolicies = securityPolicies; }
    
    // Additional methods needed by service
    public String getErrorMessage() { return message; }
    public void setErrorMessage(String errorMessage) { this.message = errorMessage; }
    
    public void setCertificates(CertificateSetup certificates) { this.certificateSetup = certificates; }
    public void setStorageConfig(SecureStorageConfig storageConfig) { this.secureStorageConfig = storageConfig; }
}