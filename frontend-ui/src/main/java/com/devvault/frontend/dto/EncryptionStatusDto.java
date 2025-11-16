package com.devvault.frontend.dto;

/**
 * DTO for encryption status information
 */
public class EncryptionStatusDto {
    private String id;
    private String algorithm;
    private String keySize;
    private String status;
    private boolean enabled;
    private String description;
    
    // Constructors
    public EncryptionStatusDto() {}
    
    public EncryptionStatusDto(String algorithm, String keySize, boolean enabled) {
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.enabled = enabled;
        this.status = enabled ? "ACTIVE" : "INACTIVE";
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public String getKeySize() { return keySize; }
    public void setKeySize(String keySize) { this.keySize = keySize; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}