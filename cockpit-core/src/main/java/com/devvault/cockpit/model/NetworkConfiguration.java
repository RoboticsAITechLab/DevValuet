package com.devvault.cockpit.model;

import java.util.List;

/**
 * Network configuration information
 */
public class NetworkConfiguration {
    private String primaryInterface;
    private List<String> availableInterfaces;
    private boolean proxyConfigured;
    private String proxyHost;
    private int proxyPort;
    private boolean firewallActive;
    private List<String> blockedPorts;
    
    // Constructors
    public NetworkConfiguration() {}
    
    public NetworkConfiguration(String primaryInterface, boolean proxyConfigured, boolean firewallActive) {
        this.primaryInterface = primaryInterface;
        this.proxyConfigured = proxyConfigured;
        this.firewallActive = firewallActive;
    }
    
    // Getters and setters
    public String getPrimaryInterface() { return primaryInterface; }
    public void setPrimaryInterface(String primaryInterface) { this.primaryInterface = primaryInterface; }
    
    public List<String> getAvailableInterfaces() { return availableInterfaces; }
    public void setAvailableInterfaces(List<String> availableInterfaces) { this.availableInterfaces = availableInterfaces; }
    
    public boolean isProxyConfigured() { return proxyConfigured; }
    public void setProxyConfigured(boolean proxyConfigured) { this.proxyConfigured = proxyConfigured; }
    
    public String getProxyHost() { return proxyHost; }
    public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }
    
    public int getProxyPort() { return proxyPort; }
    public void setProxyPort(int proxyPort) { this.proxyPort = proxyPort; }
    
    public boolean isFirewallActive() { return firewallActive; }
    public void setFirewallActive(boolean firewallActive) { this.firewallActive = firewallActive; }
    
    public List<String> getBlockedPorts() { return blockedPorts; }
    public void setBlockedPorts(List<String> blockedPorts) { this.blockedPorts = blockedPorts; }
    
    // Additional methods needed by service
    public void setHostname(String hostname) { this.primaryInterface = hostname; }
    public void setIpAddress(String ipAddress) { 
        if (availableInterfaces == null) {
            availableInterfaces = new java.util.ArrayList<>();
        }
        if (!availableInterfaces.contains(ipAddress)) {
            availableInterfaces.add(ipAddress);
        }
    }
    public void setInternetConnected(boolean internetConnected) { 
        this.firewallActive = !internetConnected; // Simple mapping for this context
    }
}