package com.devvault.frontend.services;

import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.http.HttpClientConfig;
import com.devvault.frontend.dto.DatasetDto;
import com.devvault.frontend.dto.DataQualityReportDto;
import com.devvault.frontend.dto.DataStatisticsDto;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise Dataset Service
 * Handles dataset operations, data quality checks, and analytics
 */
public class DatasetService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatasetService.class);
    
    private final HttpClientService httpClient;
    private final ObservableList<DatasetDto> datasets;
    
    // Dataset Statistics Properties
    private final LongProperty totalDatasets = new SimpleLongProperty(0);
    private final LongProperty totalStorageUsed = new SimpleLongProperty(0);
    private final DoubleProperty dataQualityScore = new SimpleDoubleProperty(0.0);
    
    private static DatasetService instance;
    
    private DatasetService() {
        this.httpClient = HttpClientService.getInstance();
        this.datasets = FXCollections.observableArrayList();
        logger.info("Enterprise DatasetService initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized DatasetService getInstance() {
        if (instance == null) {
            instance = new DatasetService();
        }
        return instance;
    }
    
    // ============================================
    // DATASET MANAGEMENT
    // ============================================
    
    /**
     * Get all datasets
     */
    public ObservableList<DatasetDto> getAllDatasets() {
        return datasets;
    }
    
    /**
     * Refresh datasets from offline-engine
     */
    public CompletableFuture<Void> refreshDatasets() {
        return httpClient.getCockpitCoreAsync("/api/datasets", new TypeReference<List<DatasetDto>>() {})
                .thenAccept(datasetList -> {
                    javafx.application.Platform.runLater(() -> {
                        datasets.clear();
                        datasets.addAll(datasetList);
                        totalDatasets.set(datasetList.size());
                    });
                    logger.info("Refreshed {} datasets from backend", datasetList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh datasets", throwable);
                    return null;
                });
    }
    
    /**
     * Import dataset from file
     */
    public CompletableFuture<DatasetDto> importDataset(String filePath, String datasetName, String format) {
        logger.info("Importing dataset: {} from {}", datasetName, filePath);
        
        DatasetImportRequest request = new DatasetImportRequest();
        request.setFilePath(filePath);
        request.setName(datasetName);
        request.setFormat(format);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/import", request, DatasetDto.class)
                .thenApply(dataset -> {
                    javafx.application.Platform.runLater(() -> {
                        datasets.add(dataset);
                        totalDatasets.set(datasets.size());
                    });
                    logger.info("Dataset imported successfully: {}", dataset.getName());
                    return dataset;
                })
                .exceptionally(throwable -> {
                    logger.error("Dataset import failed: {}", datasetName, throwable);
                    throw new RuntimeException("Dataset import failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Export dataset to file
     */
    public CompletableFuture<String> exportDataset(Long datasetId, String format, String outputPath) {
        logger.info("Exporting dataset {} to {} in {} format", datasetId, outputPath, format);
        
        DatasetExportRequest request = new DatasetExportRequest();
        request.setDatasetId(datasetId);
        request.setFormat(format);
        request.setOutputPath(outputPath);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/export", request, String.class)
                .thenApply(exportPath -> {
                    logger.info("Dataset exported successfully to: {}", exportPath);
                    return exportPath;
                })
                .exceptionally(throwable -> {
                    logger.error("Dataset export failed: {}", datasetId, throwable);
                    throw new RuntimeException("Dataset export failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Delete dataset
     */
    public CompletableFuture<Boolean> deleteDataset(Long datasetId, boolean createBackup) {
        logger.info("Deleting dataset: {} (backup: {})", datasetId, createBackup);
        
        String endpoint = createBackup ? 
                "/api/datasets/" + datasetId + "/delete?backup=true" :
                "/api/datasets/" + datasetId + "/delete";
        
        return httpClient.deleteAsync(endpoint, HttpClientConfig.COCKPIT_CORE_BASE_URL)
                .thenApply(result -> {
                    javafx.application.Platform.runLater(() -> {
                        datasets.removeIf(dataset -> dataset.getId().equals(datasetId));
                        totalDatasets.set(datasets.size());
                    });
                    logger.info("Dataset deleted successfully: {}", datasetId);
                    return true;
                })
                .exceptionally(throwable -> {
                    logger.error("Dataset deletion failed: {}", datasetId, throwable);
                    return false;
                });
    }
    
    // ============================================
    // DATA QUALITY & ANALYTICS
    // ============================================
    
    /**
     * Run data quality checks
     */
    public CompletableFuture<DataQualityReportDto> runDataQualityCheck(Long datasetId) {
        logger.info("Running data quality check for dataset: {}", datasetId);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/" + datasetId + "/quality-check", null, DataQualityReportDto.class)
                .thenApply(report -> {
                    javafx.application.Platform.runLater(() -> {
                        dataQualityScore.set(report.getOverallScore());
                    });
                    logger.info("Data quality check completed - Score: {}", report.getOverallScore());
                    return report;
                })
                .exceptionally(throwable -> {
                    logger.error("Data quality check failed for dataset: {}", datasetId, throwable);
                    throw new RuntimeException("Data quality check failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Run data cleaning process
     */
    public CompletableFuture<DataCleaningResultDto> runDataCleaning(Long datasetId, List<String> cleaningRules) {
        logger.info("Running data cleaning for dataset: {} with {} rules", datasetId, cleaningRules.size());
        
        DataCleaningRequest request = new DataCleaningRequest();
        request.setDatasetId(datasetId);
        request.setCleaningRules(cleaningRules);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/clean", request, DataCleaningResultDto.class)
                .thenApply(result -> {
                    logger.info("Data cleaning completed - {} records processed", result.getRecordsProcessed());
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("Data cleaning failed for dataset: {}", datasetId, throwable);
                    throw new RuntimeException("Data cleaning failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Get dataset statistics
     */
    public CompletableFuture<DataStatisticsDto> getDatasetStatistics(Long datasetId) {
        logger.info("Getting statistics for dataset: {}", datasetId);
        
        return httpClient.getCockpitCoreAsync("/api/datasets/" + datasetId + "/statistics", DataStatisticsDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get dataset statistics: {}", datasetId, throwable);
                    return new DataStatisticsDto(); // Return empty stats on failure
                });
    }
    
    /**
     * Run data profiling
     */
    public CompletableFuture<DataProfilingReportDto> runDataProfiling(Long datasetId) {
        logger.info("Running data profiling for dataset: {}", datasetId);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/" + datasetId + "/profile", null, DataProfilingReportDto.class)
                .thenApply(report -> {
                    logger.info("Data profiling completed for dataset: {}", datasetId);
                    return report;
                })
                .exceptionally(throwable -> {
                    logger.error("Data profiling failed for dataset: {}", datasetId, throwable);
                    throw new RuntimeException("Data profiling failed: " + throwable.getMessage(), throwable);
                });
    }
    
    // ============================================
    // DATA OPTIMIZATION
    // ============================================
    
    /**
     * Optimize dataset
     */
    public CompletableFuture<DataOptimizationResultDto> optimizeDataset(Long datasetId) {
        logger.info("Optimizing dataset: {}", datasetId);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/" + datasetId + "/optimize", null, DataOptimizationResultDto.class)
                .thenApply(result -> {
                    logger.info("Dataset optimization completed - Size reduced by {}%", result.getSizeReductionPercent());
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("Dataset optimization failed: {}", datasetId, throwable);
                    throw new RuntimeException("Dataset optimization failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Archive old datasets
     */
    public CompletableFuture<Integer> archiveOldDatasets(int daysSinceLastAccess) {
        logger.info("Archiving datasets older than {} days", daysSinceLastAccess);
        
        ArchiveRequest request = new ArchiveRequest();
        request.setDaysSinceLastAccess(daysSinceLastAccess);
        
        return httpClient.postCockpitCoreAsync("/api/datasets/archive", request, Integer.class)
                .thenApply(archivedCount -> {
                    logger.info("Archived {} datasets", archivedCount);
                    // Refresh dataset list
                    refreshDatasets();
                    return archivedCount;
                })
                .exceptionally(throwable -> {
                    logger.error("Dataset archiving failed", throwable);
                    return 0;
                });
    }
    
    // ============================================
    // ML & AI FEATURES
    // ============================================
    
    /**
     * Detect data anomalies using ML
     */
    public CompletableFuture<List<DataAnomalyDto>> detectAnomalies(Long datasetId) {
        logger.info("Running ML anomaly detection for dataset: {}", datasetId);
        
        return httpClient.getAiSubsystemAsync("/api/datasets/" + datasetId + "/anomalies", 
                new TypeReference<List<DataAnomalyDto>>() {})
                .thenApply(anomalies -> {
                    logger.info("Anomaly detection completed - {} anomalies found", anomalies.size());
                    return anomalies;
                })
                .exceptionally(throwable -> {
                    logger.error("Anomaly detection failed for dataset: {}", datasetId, throwable);
                    return List.of(); // Return empty list on failure
                });
    }
    
    /**
     * Get storage usage statistics
     */
    public CompletableFuture<StorageStatsDto> getStorageStatistics() {
        return httpClient.getCockpitCoreAsync("/api/datasets/storage/stats", StorageStatsDto.class)
                .thenApply(stats -> {
                    javafx.application.Platform.runLater(() -> {
                        totalStorageUsed.set(stats.getTotalUsedBytes());
                    });
                    return stats;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get storage statistics", throwable);
                    return new StorageStatsDto();
                });
    }
    
    /**
     * Search datasets
     */
    public CompletableFuture<List<DatasetDto>> searchDatasets(String query) {
        return httpClient.getCockpitCoreAsync("/api/datasets/search?q=" + query, 
                new TypeReference<List<DatasetDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Dataset search failed", throwable);
                    // Fallback to local search
                    return datasets.stream()
                            .filter(dataset -> 
                                dataset.getName().toLowerCase().contains(query.toLowerCase()) ||
                                dataset.getDescription().toLowerCase().contains(query.toLowerCase())
                            )
                            .toList();
                });
    }
    
    // ============================================
    // PROPERTY ACCESSORS
    // ============================================
    
    public LongProperty totalDatasetsProperty() { return totalDatasets; }
    public long getTotalDatasets() { return totalDatasets.get(); }
    
    public LongProperty totalStorageUsedProperty() { return totalStorageUsed; }
    public long getTotalStorageUsed() { return totalStorageUsed.get(); }
    
    public DoubleProperty dataQualityScoreProperty() { return dataQualityScore; }
    public double getDataQualityScore() { return dataQualityScore.get(); }
    
    // ============================================
    // DTO CLASSES
    // ============================================
    
    private static class DatasetImportRequest {
        private String filePath;
        private String name;
        private String format;
        
        public String getFilePath() { return filePath; }
        public void setFilePath(String filePath) { this.filePath = filePath; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
    }
    
    private static class DatasetExportRequest {
        private Long datasetId;
        private String format;
        private String outputPath;
        
        public Long getDatasetId() { return datasetId; }
        public void setDatasetId(Long datasetId) { this.datasetId = datasetId; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getOutputPath() { return outputPath; }
        public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
    }
    
    private static class DataCleaningRequest {
        private Long datasetId;
        private List<String> cleaningRules;
        
        public Long getDatasetId() { return datasetId; }
        public void setDatasetId(Long datasetId) { this.datasetId = datasetId; }
        
        public List<String> getCleaningRules() { return cleaningRules; }
        public void setCleaningRules(List<String> cleaningRules) { this.cleaningRules = cleaningRules; }
    }
    
    private static class ArchiveRequest {
        private int daysSinceLastAccess;
        
        public int getDaysSinceLastAccess() { return daysSinceLastAccess; }
        public void setDaysSinceLastAccess(int daysSinceLastAccess) { this.daysSinceLastAccess = daysSinceLastAccess; }
    }
    
    public static class DataCleaningResultDto {
        private int recordsProcessed;
        private int recordsCleaned;
        private int recordsRemoved;
        
        public int getRecordsProcessed() { return recordsProcessed; }
        public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }
        
        public int getRecordsCleaned() { return recordsCleaned; }
        public void setRecordsCleaned(int recordsCleaned) { this.recordsCleaned = recordsCleaned; }
        
        public int getRecordsRemoved() { return recordsRemoved; }
        public void setRecordsRemoved(int recordsRemoved) { this.recordsRemoved = recordsRemoved; }
    }
    
    public static class DataProfilingReportDto {
        private String datasetName;
        private int totalColumns;
        private int totalRows;
        private List<ColumnProfileDto> columnProfiles;
        
        public String getDatasetName() { return datasetName; }
        public void setDatasetName(String datasetName) { this.datasetName = datasetName; }
        
        public int getTotalColumns() { return totalColumns; }
        public void setTotalColumns(int totalColumns) { this.totalColumns = totalColumns; }
        
        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        
        public List<ColumnProfileDto> getColumnProfiles() { return columnProfiles; }
        public void setColumnProfiles(List<ColumnProfileDto> columnProfiles) { this.columnProfiles = columnProfiles; }
    }
    
    public static class DataOptimizationResultDto {
        private long originalSizeBytes;
        private long optimizedSizeBytes;
        private double sizeReductionPercent;
        
        public long getOriginalSizeBytes() { return originalSizeBytes; }
        public void setOriginalSizeBytes(long originalSizeBytes) { this.originalSizeBytes = originalSizeBytes; }
        
        public long getOptimizedSizeBytes() { return optimizedSizeBytes; }
        public void setOptimizedSizeBytes(long optimizedSizeBytes) { this.optimizedSizeBytes = optimizedSizeBytes; }
        
        public double getSizeReductionPercent() { return sizeReductionPercent; }
        public void setSizeReductionPercent(double sizeReductionPercent) { this.sizeReductionPercent = sizeReductionPercent; }
    }
    
    public static class DataAnomalyDto {
        private String columnName;
        private String anomalyType;
        private String description;
        private double confidenceScore;
        
        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
        
        public String getAnomalyType() { return anomalyType; }
        public void setAnomalyType(String anomalyType) { this.anomalyType = anomalyType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public double getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    }
    
    public static class StorageStatsDto {
        private long totalUsedBytes;
        private long availableBytes;
        private int totalDatasets;
        private double averageDatasetSize;
        
        public long getTotalUsedBytes() { return totalUsedBytes; }
        public void setTotalUsedBytes(long totalUsedBytes) { this.totalUsedBytes = totalUsedBytes; }
        
        public long getAvailableBytes() { return availableBytes; }
        public void setAvailableBytes(long availableBytes) { this.availableBytes = availableBytes; }
        
        public int getTotalDatasets() { return totalDatasets; }
        public void setTotalDatasets(int totalDatasets) { this.totalDatasets = totalDatasets; }
        
        public double getAverageDatasetSize() { return averageDatasetSize; }
        public void setAverageDatasetSize(double averageDatasetSize) { this.averageDatasetSize = averageDatasetSize; }
    }
    
    public static class ColumnProfileDto {
        private String columnName;
        private String dataType;
        private int nullCount;
        private int uniqueCount;
        private String mostCommonValue;
        
        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
        
        public String getDataType() { return dataType; }
        public void setDataType(String dataType) { this.dataType = dataType; }
        
        public int getNullCount() { return nullCount; }
        public void setNullCount(int nullCount) { this.nullCount = nullCount; }
        
        public int getUniqueCount() { return uniqueCount; }
        public void setUniqueCount(int uniqueCount) { this.uniqueCount = uniqueCount; }
        
        public String getMostCommonValue() { return mostCommonValue; }
        public void setMostCommonValue(String mostCommonValue) { this.mostCommonValue = mostCommonValue; }
    }
}