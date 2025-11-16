package com.devvault.cockpit.model;

import java.util.Map;

/**
 * Advanced encryption keys management and configuration
 */
public class EncryptionKeys {
    private String algorithm;
    private int keySize;
    private String publicKey;
    private String privateKey;
    private String keyStoreLocation;
    private String keyStorePassword;
    private Map<String, String> certificates;
    
    // Constructors
    public EncryptionKeys() {}
    
    // Getters and Setters
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public int getKeySize() { return keySize; }
    public void setKeySize(int keySize) { this.keySize = keySize; }
    
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
    
    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }
    
    public String getKeyStoreLocation() { return keyStoreLocation; }
    public void setKeyStoreLocation(String keyStoreLocation) { this.keyStoreLocation = keyStoreLocation; }
    
    public String getKeyStorePassword() { return keyStorePassword; }
    public void setKeyStorePassword(String keyStorePassword) { this.keyStorePassword = keyStorePassword; }
    
    public Map<String, String> getCertificates() { return certificates; }
    public void setCertificates(Map<String, String> certificates) { this.certificates = certificates; }
    
    // Additional methods needed by service
    public void setAesKey(String aesKey) { 
        this.publicKey = aesKey; // Store AES key in public key field for simplicity
        this.algorithm = "AES";
    }
    
    public void setRsaPublicKey(String rsaPublicKey) { 
        this.publicKey = rsaPublicKey;
        this.algorithm = "RSA";
    }
    
    public void setRsaPrivateKey(String rsaPrivateKey) { 
        this.privateKey = rsaPrivateKey;
    }
    
    public boolean isGenerated() { 
        return this.publicKey != null && !this.publicKey.isEmpty();
    }
    
    public void setGenerated(boolean generated) { 
        // This is a computed property, so we don't store it
    }
}