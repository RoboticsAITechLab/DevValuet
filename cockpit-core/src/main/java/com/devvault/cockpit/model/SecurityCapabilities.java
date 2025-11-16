package com.devvault.cockpit.model;

import java.util.List;

/**
 * Security capabilities information
 */
public class SecurityCapabilities {
    private boolean hardwareEncryption;
    private boolean biometricSupport;
    private boolean tpmAvailable;
    private boolean secureBootEnabled;
    private List<String> supportedAlgorithms;
    private List<String> availableProviders;
    
    // Constructors
    public SecurityCapabilities() {}
    
    public SecurityCapabilities(boolean hardwareEncryption, boolean biometricSupport, boolean tpmAvailable) {
        this.hardwareEncryption = hardwareEncryption;
        this.biometricSupport = biometricSupport;
        this.tpmAvailable = tpmAvailable;
    }
    
    // Getters and setters
    public boolean isHardwareEncryption() { return hardwareEncryption; }
    public void setHardwareEncryption(boolean hardwareEncryption) { this.hardwareEncryption = hardwareEncryption; }
    
    public boolean isBiometricSupport() { return biometricSupport; }
    public void setBiometricSupport(boolean biometricSupport) { this.biometricSupport = biometricSupport; }
    
    public boolean isTpmAvailable() { return tpmAvailable; }
    public void setTpmAvailable(boolean tpmAvailable) { this.tpmAvailable = tpmAvailable; }
    
    public boolean isSecureBootEnabled() { return secureBootEnabled; }
    public void setSecureBootEnabled(boolean secureBootEnabled) { this.secureBootEnabled = secureBootEnabled; }
    
    public List<String> getSupportedAlgorithms() { return supportedAlgorithms; }
    public void setSupportedAlgorithms(List<String> supportedAlgorithms) { this.supportedAlgorithms = supportedAlgorithms; }
    
    public List<String> getAvailableProviders() { return availableProviders; }
    public void setAvailableProviders(List<String> availableProviders) { this.availableProviders = availableProviders; }
    
    // Additional methods needed by service
    public void setFingerprintSupported(boolean fingerprintSupported) { this.biometricSupport = fingerprintSupported; }
    public void setCameraSupported(boolean cameraSupported) { 
        // Store in available providers for simplicity
        if (availableProviders == null) {
            availableProviders = new java.util.ArrayList<>();
        }
        if (cameraSupported && !availableProviders.contains("camera")) {
            availableProviders.add("camera");
        } else if (!cameraSupported) {
            availableProviders.removeIf(p -> "camera".equals(p));
        }
    }
    
    public void setMicrophoneSupported(boolean microphoneSupported) { 
        // Store in available providers for simplicity
        if (availableProviders == null) {
            availableProviders = new java.util.ArrayList<>();
        }
        if (microphoneSupported && !availableProviders.contains("microphone")) {
            availableProviders.add("microphone");
        } else if (!microphoneSupported) {
            availableProviders.removeIf(p -> "microphone".equals(p));
        }
    }
    
    public void setAesEncryptionSupported(boolean aesSupported) { 
        if (supportedAlgorithms == null) {
            supportedAlgorithms = new java.util.ArrayList<>();
        }
        if (aesSupported && !supportedAlgorithms.contains("AES")) {
            supportedAlgorithms.add("AES");
        } else if (!aesSupported) {
            supportedAlgorithms.removeIf(a -> "AES".equals(a));
        }
    }
    
    public void setRsaEncryptionSupported(boolean rsaSupported) { 
        if (supportedAlgorithms == null) {
            supportedAlgorithms = new java.util.ArrayList<>();
        }
        if (rsaSupported && !supportedAlgorithms.contains("RSA")) {
            supportedAlgorithms.add("RSA");
        } else if (!rsaSupported) {
            supportedAlgorithms.removeIf(a -> "RSA".equals(a));
        }
    }
    
    public void setEccEncryptionSupported(boolean eccSupported) { 
        if (supportedAlgorithms == null) {
            supportedAlgorithms = new java.util.ArrayList<>();
        }
        if (eccSupported && !supportedAlgorithms.contains("ECC")) {
            supportedAlgorithms.add("ECC");
        } else if (!eccSupported) {
            supportedAlgorithms.removeIf(a -> "ECC".equals(a));
        }
    }
}