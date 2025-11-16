package com.devvault.frontend.services;

import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.dto.SystemMetricsDto;
import com.devvault.frontend.dto.ApplicationHealthDto;
import com.devvault.frontend.dto.AlertDto;
import com.devvault.frontend.dto.PerformanceReportDto;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise Monitoring Service
 * Handles system monitoring, alerts, and performance tracking
 */
public class MonitoringService {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringService.class);
    
    private final HttpClientService httpClient;
    private final ObservableList<AlertDto> activeAlerts;
    private final ObservableList<SystemMetricsDto> metricsHistory;
    
    // System Status Properties
    private final DoubleProperty cpuUsage = new SimpleDoubleProperty(0.0);
    private final DoubleProperty memoryUsage = new SimpleDoubleProperty(0.0);
    private final DoubleProperty diskUsage = new SimpleDoubleProperty(0.0);
    private final StringProperty systemHealth = new SimpleStringProperty("UNKNOWN");
    private final IntegerProperty activeAlertsCount = new SimpleIntegerProperty(0);
    private final BooleanProperty monitoringEnabled = new SimpleBooleanProperty(true);
    
    private static MonitoringService instance;
    
    private MonitoringService() {
        this.httpClient = HttpClientService.getInstance();
        this.activeAlerts = FXCollections.observableArrayList();
        this.metricsHistory = FXCollections.observableArrayList();
        logger.info("Enterprise MonitoringService initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized MonitoringService getInstance() {
        if (instance == null) {
            instance = new MonitoringService();
        }
        return instance;
    }
    
    // ============================================
    // SYSTEM MONITORING
    // ============================================
    
    /**
     * Get current system metrics
     */
    public CompletableFuture<SystemMetricsDto> getCurrentSystemMetrics() {
        return httpClient.getCockpitCoreAsync("/api/monitoring/metrics/system", SystemMetricsDto.class)
                .thenApply(metrics -> {
                    javafx.application.Platform.runLater(() -> {
                        cpuUsage.set(metrics.getCpuUsagePercent());
                        memoryUsage.set(metrics.getMemoryUsagePercent());
                        diskUsage.set(metrics.getDiskUsagePercent());
                        systemHealth.set(String.valueOf(metrics.getOverallHealth()));
                        
                        // Add to history
                        metricsHistory.add(metrics);
                        if (metricsHistory.size() > 100) { // Keep last 100 entries
                            metricsHistory.remove(0);
                        }
                    });
                    return metrics;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get system metrics", throwable);
                    return new SystemMetricsDto();
                });
    }
    
    /**
     * Get application health status
     */
    public CompletableFuture<ApplicationHealthDto> getApplicationHealth() {
        return httpClient.getCockpitCoreAsync("/api/monitoring/health", ApplicationHealthDto.class)
                .thenApply(health -> {
                    javafx.application.Platform.runLater(() -> {
                        systemHealth.set(health.getOverallStatus());
                    });
                    return health;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get application health", throwable);
                    return new ApplicationHealthDto();
                });
    }
    
    /**
     * Get detailed service health
     */
    public CompletableFuture<Map<String, ServiceHealthDto>> getDetailedServiceHealth() {
        return httpClient.getCockpitCoreAsync("/api/monitoring/health/services", 
                new TypeReference<Map<String, ServiceHealthDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get detailed service health", throwable);
                    return Map.of();
                });
    }
    
    /**
     * Get performance metrics
     */
    public CompletableFuture<PerformanceReportDto> getPerformanceMetrics(String timeRange) {
        return httpClient.getCockpitCoreAsync("/api/monitoring/performance?timeRange=" + timeRange, PerformanceReportDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get performance metrics", throwable);
                    return new PerformanceReportDto();
                });
    }
    
    /**
     * Start system monitoring
     */
    public CompletableFuture<Boolean> startMonitoring(int intervalSeconds) {
        logger.info("Starting system monitoring with {} second intervals", intervalSeconds);
        
        MonitoringConfigRequest request = new MonitoringConfigRequest();
        request.setIntervalSeconds(intervalSeconds);
        request.setEnabled(true);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/start", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            monitoringEnabled.set(true);
                        });
                        logger.info("System monitoring started successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to start monitoring", throwable);
                    return false;
                });
    }
    
    /**
     * Stop system monitoring
     */
    public CompletableFuture<Boolean> stopMonitoring() {
        logger.info("Stopping system monitoring");
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/stop", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            monitoringEnabled.set(false);
                        });
                        logger.info("System monitoring stopped successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to stop monitoring", throwable);
                    return false;
                });
    }
    
    // ============================================
    // ALERT MANAGEMENT
    // ============================================
    
    /**
     * Get active alerts
     */
    public ObservableList<AlertDto> getActiveAlerts() {
        return activeAlerts;
    }
    
    /**
     * Refresh active alerts
     */
    public CompletableFuture<Void> refreshActiveAlerts() {
        return httpClient.getCockpitCoreAsync("/api/monitoring/alerts/active", new TypeReference<List<AlertDto>>() {})
                .thenAccept(alertList -> {
                    javafx.application.Platform.runLater(() -> {
                        activeAlerts.clear();
                        activeAlerts.addAll(alertList);
                        activeAlertsCount.set(alertList.size());
                    });
                    logger.info("Refreshed {} active alerts", alertList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh active alerts", throwable);
                    return null;
                });
    }
    
    /**
     * Create custom alert
     */
    public CompletableFuture<AlertDto> createAlert(String type, String message, String severity, Map<String, Object> metadata) {
        logger.info("Creating alert: {} - {}", severity, message);
        
        AlertCreateRequest request = new AlertCreateRequest();
        request.setType(type);
        request.setMessage(message);
        request.setSeverity(severity);
        request.setMetadata(metadata);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/alerts/create", request, AlertDto.class)
                .thenApply(alert -> {
                    javafx.application.Platform.runLater(() -> {
                        activeAlerts.add(alert);
                        activeAlertsCount.set(activeAlerts.size());
                    });
                    logger.info("Alert created successfully: {}", alert.getId());
                    return alert;
                })
                .exceptionally(throwable -> {
                    logger.error("Alert creation failed: {}", message, throwable);
                    throw new RuntimeException("Alert creation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Acknowledge alert
     */
    public CompletableFuture<Boolean> acknowledgeAlert(String alertId, String acknowledgedBy) {
        logger.info("Acknowledging alert: {} by {}", alertId, acknowledgedBy);
        
        AlertAckRequest request = new AlertAckRequest();
        request.setAlertId(alertId);
        request.setAcknowledgedBy(acknowledgedBy);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/alerts/acknowledge", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            activeAlerts.stream()
                                    .filter(alert -> alert.getId().equals(alertId))
                                    .findFirst()
                                    .ifPresent(alert -> {
                                        alert.setAcknowledged(true);
                                        alert.setAcknowledgedBy(acknowledgedBy);
                                    });
                        });
                        logger.info("Alert acknowledged successfully: {}", alertId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Alert acknowledgment failed: {}", alertId, throwable);
                    return false;
                });
    }
    
    /**
     * Resolve alert
     */
    public CompletableFuture<Boolean> resolveAlert(String alertId, String resolvedBy, String resolution) {
        logger.info("Resolving alert: {} by {}", alertId, resolvedBy);
        
        AlertResolveRequest request = new AlertResolveRequest();
        request.setAlertId(alertId);
        request.setResolvedBy(resolvedBy);
        request.setResolution(resolution);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/alerts/resolve", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            activeAlerts.removeIf(alert -> alert.getId().equals(alertId));
                            activeAlertsCount.set(activeAlerts.size());
                        });
                        logger.info("Alert resolved successfully: {}", alertId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Alert resolution failed: {}", alertId, throwable);
                    return false;
                });
    }
    
    /**
     * Get alert history
     */
    public CompletableFuture<List<AlertDto>> getAlertHistory(String timeRange, String severity) {
        String endpoint = String.format("/api/monitoring/alerts/history?timeRange=%s&severity=%s", timeRange, severity);
        
        return httpClient.getCockpitCoreAsync(endpoint, new TypeReference<List<AlertDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get alert history", throwable);
                    return List.of();
                });
    }
    
    // ============================================
    // THRESHOLD MANAGEMENT
    // ============================================
    
    /**
     * Set monitoring thresholds
     */
    public CompletableFuture<Boolean> setMonitoringThresholds(Map<String, ThresholdDto> thresholds) {
        logger.info("Setting monitoring thresholds for {} metrics", thresholds.size());
        
        ThresholdUpdateRequest request = new ThresholdUpdateRequest();
        request.setThresholds(thresholds);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/thresholds", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Monitoring thresholds updated successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to update monitoring thresholds", throwable);
                    return false;
                });
    }
    
    /**
     * Get current thresholds
     */
    public CompletableFuture<Map<String, ThresholdDto>> getCurrentThresholds() {
        return httpClient.getCockpitCoreAsync("/api/monitoring/thresholds", 
                new TypeReference<Map<String, ThresholdDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get current thresholds", throwable);
                    return Map.of();
                });
    }
    
    // ============================================
    // LOG MONITORING
    // ============================================
    
    /**
     * Get system logs
     */
    public CompletableFuture<List<LogEntryDto>> getSystemLogs(String level, int limit) {
        String endpoint = String.format("/api/monitoring/logs?level=%s&limit=%d", level, limit);
        
        return httpClient.getCockpitCoreAsync(endpoint, new TypeReference<List<LogEntryDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get system logs", throwable);
                    return List.of();
                });
    }
    
    /**
     * Get application logs
     */
    public CompletableFuture<List<LogEntryDto>> getApplicationLogs(String service, String level, int limit) {
        String endpoint = String.format("/api/monitoring/logs/application?service=%s&level=%s&limit=%d", 
                service, level, limit);
        
        return httpClient.getCockpitCoreAsync(endpoint, new TypeReference<List<LogEntryDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get application logs", throwable);
                    return List.of();
                });
    }
    
    /**
     * Search logs
     */
    public CompletableFuture<List<LogEntryDto>> searchLogs(String query, String timeRange) {
        String endpoint = String.format("/api/monitoring/logs/search?q=%s&timeRange=%s", query, timeRange);
        
        return httpClient.getCockpitCoreAsync(endpoint, new TypeReference<List<LogEntryDto>>() {})
                .thenApply(logs -> {
                    logger.info("Found {} log entries matching query: {}", logs.size(), query);
                    return logs;
                })
                .exceptionally(throwable -> {
                    logger.error("Log search failed: {}", query, throwable);
                    return List.of();
                });
    }
    
    // ============================================
    // REPORTING
    // ============================================
    
    /**
     * Generate performance report
     */
    public CompletableFuture<PerformanceReportDto> generatePerformanceReport(String timeRange, List<String> metrics) {
        logger.info("Generating performance report for time range: {}", timeRange);
        
        ReportGenerateRequest request = new ReportGenerateRequest();
        request.setTimeRange(timeRange);
        request.setMetrics(metrics);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/reports/performance", request, PerformanceReportDto.class)
                .thenApply(report -> {
                    logger.info("Performance report generated successfully");
                    return report;
                })
                .exceptionally(throwable -> {
                    logger.error("Performance report generation failed", throwable);
                    throw new RuntimeException("Performance report generation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Export monitoring data
     */
    public CompletableFuture<String> exportMonitoringData(String format, String timeRange, List<String> dataTypes) {
        logger.info("Exporting monitoring data in {} format", format);
        
        DataExportRequest request = new DataExportRequest();
        request.setFormat(format);
        request.setTimeRange(timeRange);
        request.setDataTypes(dataTypes);
        
        return httpClient.postCockpitCoreAsync("/api/monitoring/export", request, String.class)
                .thenApply(exportPath -> {
                    logger.info("Monitoring data exported successfully to: {}", exportPath);
                    return exportPath;
                })
                .exceptionally(throwable -> {
                    logger.error("Monitoring data export failed", throwable);
                    throw new RuntimeException("Monitoring data export failed: " + throwable.getMessage(), throwable);
                });
    }
    
    // ============================================
    // ANOMALY DETECTION
    // ============================================
    
    /**
     * Detect performance anomalies
     */
    public CompletableFuture<List<AnomalyDto>> detectAnomalies(String timeRange) {
        logger.info("Running anomaly detection for time range: {}", timeRange);
        
        return httpClient.getAiSubsystemAsync("/api/monitoring/anomalies?timeRange=" + timeRange, 
                new TypeReference<List<AnomalyDto>>() {})
                .thenApply(anomalies -> {
                    logger.info("Anomaly detection completed - {} anomalies found", anomalies.size());
                    return anomalies;
                })
                .exceptionally(throwable -> {
                    logger.error("Anomaly detection failed", throwable);
                    return List.of();
                });
    }
    
    /**
     * Get predictive insights
     */
    public CompletableFuture<PredictiveInsightsDto> getPredictiveInsights(String metric, int forecastHours) {
        String endpoint = String.format("/api/monitoring/insights/predictive?metric=%s&hours=%d", metric, forecastHours);
        
        return httpClient.getAiSubsystemAsync(endpoint, PredictiveInsightsDto.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get predictive insights", throwable);
                    return new PredictiveInsightsDto();
                });
    }
    
    // ============================================
    // PROPERTY ACCESSORS
    // ============================================
    
    public DoubleProperty cpuUsageProperty() { return cpuUsage; }
    public double getCpuUsage() { return cpuUsage.get(); }
    
    public DoubleProperty memoryUsageProperty() { return memoryUsage; }
    public double getMemoryUsage() { return memoryUsage.get(); }
    
    public DoubleProperty diskUsageProperty() { return diskUsage; }
    public double getDiskUsage() { return diskUsage.get(); }
    
    public StringProperty systemHealthProperty() { return systemHealth; }
    public String getSystemHealth() { return systemHealth.get(); }
    
    public IntegerProperty activeAlertsCountProperty() { return activeAlertsCount; }
    public int getActiveAlertsCount() { return activeAlertsCount.get(); }
    
    public BooleanProperty monitoringEnabledProperty() { return monitoringEnabled; }
    public boolean isMonitoringEnabled() { return monitoringEnabled.get(); }
    
    public ObservableList<SystemMetricsDto> getMetricsHistory() { return metricsHistory; }
    
    // ============================================
    // DTO CLASSES
    // ============================================
    
    private static class MonitoringConfigRequest {
        private int intervalSeconds;
        private boolean enabled;
        
        public int getIntervalSeconds() { return intervalSeconds; }
        public void setIntervalSeconds(int intervalSeconds) { this.intervalSeconds = intervalSeconds; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    private static class AlertCreateRequest {
        private String type;
        private String message;
        private String severity;
        private Map<String, Object> metadata;
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    private static class AlertAckRequest {
        private String alertId;
        private String acknowledgedBy;
        
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        
        public String getAcknowledgedBy() { return acknowledgedBy; }
        public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
    }
    
    private static class AlertResolveRequest {
        private String alertId;
        private String resolvedBy;
        private String resolution;
        
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        
        public String getResolvedBy() { return resolvedBy; }
        public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
        
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
    }
    
    private static class ThresholdUpdateRequest {
        private Map<String, ThresholdDto> thresholds;
        
        public Map<String, ThresholdDto> getThresholds() { return thresholds; }
        public void setThresholds(Map<String, ThresholdDto> thresholds) { this.thresholds = thresholds; }
    }
    
    private static class ReportGenerateRequest {
        private String timeRange;
        private List<String> metrics;
        
        public String getTimeRange() { return timeRange; }
        public void setTimeRange(String timeRange) { this.timeRange = timeRange; }
        
        public List<String> getMetrics() { return metrics; }
        public void setMetrics(List<String> metrics) { this.metrics = metrics; }
    }
    
    private static class DataExportRequest {
        private String format;
        private String timeRange;
        private List<String> dataTypes;
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getTimeRange() { return timeRange; }
        public void setTimeRange(String timeRange) { this.timeRange = timeRange; }
        
        public List<String> getDataTypes() { return dataTypes; }
        public void setDataTypes(List<String> dataTypes) { this.dataTypes = dataTypes; }
    }
    
    public static class ServiceHealthDto {
        private String status;
        private String message;
        private long responseTime;
        private Map<String, Object> details;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public long getResponseTime() { return responseTime; }
        public void setResponseTime(long responseTime) { this.responseTime = responseTime; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
    
    public static class ThresholdDto {
        private double warningLevel;
        private double criticalLevel;
        private String unit;
        private boolean enabled;
        
        public double getWarningLevel() { return warningLevel; }
        public void setWarningLevel(double warningLevel) { this.warningLevel = warningLevel; }
        
        public double getCriticalLevel() { return criticalLevel; }
        public void setCriticalLevel(double criticalLevel) { this.criticalLevel = criticalLevel; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
    
    public static class LogEntryDto {
        private String timestamp;
        private String level;
        private String message;
        private String service;
        private Map<String, Object> metadata;
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getService() { return service; }
        public void setService(String service) { this.service = service; }
        
        public Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    }
    
    public static class AnomalyDto {
        private String metric;
        private double actualValue;
        private double expectedValue;
        private double deviation;
        private String severity;
        private String timestamp;
        
        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }
        
        public double getActualValue() { return actualValue; }
        public void setActualValue(double actualValue) { this.actualValue = actualValue; }
        
        public double getExpectedValue() { return expectedValue; }
        public void setExpectedValue(double expectedValue) { this.expectedValue = expectedValue; }
        
        public double getDeviation() { return deviation; }
        public void setDeviation(double deviation) { this.deviation = deviation; }
        
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
    
    public static class PredictiveInsightsDto {
        private String metric;
        private List<Double> forecast;
        private double confidence;
        private List<String> recommendations;
        
        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }
        
        public List<Double> getForecast() { return forecast; }
        public void setForecast(List<Double> forecast) { this.forecast = forecast; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
        
        public List<String> getRecommendations() { return recommendations; }
        public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    }
}