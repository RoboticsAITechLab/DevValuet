package com.devvault.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * UserDto for backend-core module - simplified version
 */
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    
    // Biometric and behavioral data for AI processing
    private Map<String, Object> biometricProfile;
    private Map<String, Object> behavioralAnalytics;
    private List<String> securityRoles;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(String username, String email) {
        this.username = username;
        this.email = email;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    
    public Map<String, Object> getBiometricProfile() { return biometricProfile; }
    public void setBiometricProfile(Map<String, Object> biometricProfile) { this.biometricProfile = biometricProfile; }
    
    public Map<String, Object> getBehavioralAnalytics() { return behavioralAnalytics; }
    public void setBehavioralAnalytics(Map<String, Object> behavioralAnalytics) { this.behavioralAnalytics = behavioralAnalytics; }
    
    public List<String> getSecurityRoles() { return securityRoles; }
    public void setSecurityRoles(List<String> securityRoles) { this.securityRoles = securityRoles; }
}