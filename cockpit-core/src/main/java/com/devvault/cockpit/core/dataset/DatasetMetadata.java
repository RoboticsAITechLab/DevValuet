package com.devvault.cockpit.core.dataset;

import java.util.List;

public class DatasetMetadata {
    private long fileCount;
    private long totalSizeBytes;
    private List<String> sampleFiles;

    public DatasetMetadata() {}

    public DatasetMetadata(long fileCount, long totalSizeBytes, List<String> sampleFiles) {
        this.fileCount = fileCount;
        this.totalSizeBytes = totalSizeBytes;
        this.sampleFiles = sampleFiles;
    }

    public long getFileCount() { return fileCount; }
    public void setFileCount(long fileCount) { this.fileCount = fileCount; }
    public long getTotalSizeBytes() { return totalSizeBytes; }
    public void setTotalSizeBytes(long totalSizeBytes) { this.totalSizeBytes = totalSizeBytes; }
    public List<String> getSampleFiles() { return sampleFiles; }
    public void setSampleFiles(List<String> sampleFiles) { this.sampleFiles = sampleFiles; }
}
