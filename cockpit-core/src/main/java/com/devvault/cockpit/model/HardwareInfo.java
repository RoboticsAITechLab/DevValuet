package com.devvault.cockpit.model;

import java.util.List;

/**
 * Hardware information
 */
public class HardwareInfo {
    private String cpu;
    private int cores;
    private long totalMemory;
    private long availableMemory;
    private List<DiskInfo> disks;
    private boolean biometricSupported;
    private boolean tpmAvailable;
    private String gpuModel;
    private long availableDisk;
    
    // Constructors
    public HardwareInfo() {}
    
    public HardwareInfo(String cpu, int cores, long totalMemory) {
        this.cpu = cpu;
        this.cores = cores;
        this.totalMemory = totalMemory;
    }
    
    // Getters and setters
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    
    public int getCores() { return cores; }
    public void setCores(int cores) { this.cores = cores; }
    
    public long getTotalMemory() { return totalMemory; }
    public void setTotalMemory(long totalMemory) { this.totalMemory = totalMemory; }
    
    public long getAvailableMemory() { return availableMemory; }
    public void setAvailableMemory(long availableMemory) { this.availableMemory = availableMemory; }
    
    public List<DiskInfo> getDisks() { return disks; }
    public void setDisks(List<DiskInfo> disks) { this.disks = disks; }
    
    public boolean isBiometricSupported() { return biometricSupported; }
    public void setBiometricSupported(boolean biometricSupported) { this.biometricSupported = biometricSupported; }
    
    public boolean isTmpAvailable() { return tpmAvailable; }
    public void setTmpAvailable(boolean tmpAvailable) { this.tpmAvailable = tmpAvailable; }
    
    public boolean isTpmAvailable() { return tpmAvailable; }
    public void setTpmAvailable(boolean tpmAvailable) { this.tpmAvailable = tpmAvailable; }
    
    // Additional methods needed by service
    public void setCpuCores(int cpuCores) { this.cores = cpuCores; }
    public void setMaxMemoryMB(long maxMemoryMB) { this.totalMemory = maxMemoryMB; }
    public void setTotalMemoryMB(long totalMemoryMB) { this.totalMemory = totalMemoryMB; }
    public void setFreeMemoryMB(long freeMemoryMB) { this.availableMemory = freeMemoryMB; }
    public void setDiskInfos(List<DiskInfo> diskInfos) { this.disks = diskInfos; }
    
    // Missing methods required by SystemRequirementsUI
    public String getCpuModel() { return cpu; }
    public int getCpuCores() { return cores; }
    public String getGpuModel() { return gpuModel != null ? gpuModel : "Integrated Graphics"; }
    public void setGpuModel(String gpuModel) { this.gpuModel = gpuModel; }
    public long getAvailableDisk() { 
        if (disks != null && !disks.isEmpty()) {
            return disks.get(0).getFreeSpace() / (1024 * 1024 * 1024); // Convert to GB
        }
        return availableDisk;
    }
    public void setAvailableDisk(long availableDisk) { this.availableDisk = availableDisk; }
}