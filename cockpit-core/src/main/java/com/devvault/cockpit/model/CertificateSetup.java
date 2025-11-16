package com.devvault.cockpit.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Certificate setup and management configuration
 */
public class CertificateSetup {
    private String certificateType;
    private String issuer;
    private String subject;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String certificatePath;
    private Map<String, String> extensions;
    
    // Constructors
    public CertificateSetup() {}
    
    // Getters and Setters
    public String getCertificateType() { return certificateType; }
    public void setCertificateType(String certificateType) { this.certificateType = certificateType; }
    
    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
    
    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
    
    public String getCertificatePath() { return certificatePath; }
    public void setCertificatePath(String certificatePath) { this.certificatePath = certificatePath; }
    
    public Map<String, String> getExtensions() { return extensions; }
    public void setExtensions(Map<String, String> extensions) { this.extensions = extensions; }
}