package com.devvault.frontend.services;

import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.dto.AIModelDto;
import com.devvault.frontend.dto.PredictionResultDto;
import com.devvault.frontend.dto.TrainingJobDto;
import com.devvault.frontend.dto.AIInsightDto;
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
 * Enterprise AI Service
 * Handles AI operations, model management, and intelligent features
 */
public class AIService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    
    private final HttpClientService httpClient;
    private final ObservableList<AIModelDto> availableModels;
    private final ObservableList<TrainingJobDto> trainingJobs;
    
    // AI Status Properties
    private final StringProperty aiSystemStatus = new SimpleStringProperty("DISCONNECTED");
    private final IntegerProperty activeModels = new SimpleIntegerProperty(0);
    private final IntegerProperty runningJobs = new SimpleIntegerProperty(0);
    private final DoubleProperty systemLoad = new SimpleDoubleProperty(0.0);
    private final BooleanProperty autoLearningEnabled = new SimpleBooleanProperty(false);
    
    private static AIService instance;
    
    private AIService() {
        this.httpClient = HttpClientService.getInstance();
        this.availableModels = FXCollections.observableArrayList();
        this.trainingJobs = FXCollections.observableArrayList();
        logger.info("Enterprise AIService initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized AIService getInstance() {
        if (instance == null) {
            instance = new AIService();
        }
        return instance;
    }
    
    // ============================================
    // AI SYSTEM STATUS
    // ============================================
    
    /**
     * Check AI subsystem health
     */
    public CompletableFuture<AISystemHealthDto> checkAISystemHealth() {
        return httpClient.getAiSubsystemAsync("/api/ai/health", AISystemHealthDto.class)
                .thenApply(health -> {
                    javafx.application.Platform.runLater(() -> {
                        aiSystemStatus.set(health.getStatus());
                        systemLoad.set(health.getSystemLoad());
                        activeModels.set(health.getActiveModels());
                    });
                    logger.info("AI System Health: {} - Load: {}%", health.getStatus(), health.getSystemLoad());
                    return health;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to check AI system health", throwable);
                    javafx.application.Platform.runLater(() -> {
                        aiSystemStatus.set("ERROR");
                    });
                    return new AISystemHealthDto();
                });
    }
    
    /**
     * Initialize AI subsystem
     */
    public CompletableFuture<Boolean> initializeAISubsystem() {
        logger.info("Initializing AI subsystem");
        
        return httpClient.postAiSubsystemAsync("/api/ai/initialize", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            aiSystemStatus.set("INITIALIZED");
                        });
                        logger.info("AI subsystem initialized successfully");
                        // Load available models after initialization
                        refreshAvailableModels();
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("AI subsystem initialization failed", throwable);
                    return false;
                });
    }
    
    // ============================================
    // MODEL MANAGEMENT
    // ============================================
    
    /**
     * Get available models
     */
    public ObservableList<AIModelDto> getAvailableModels() {
        return availableModels;
    }
    
    /**
     * Refresh available AI models
     */
    public CompletableFuture<Void> refreshAvailableModels() {
        return httpClient.getAiSubsystemAsync("/api/ai/models", new TypeReference<List<AIModelDto>>() {})
                .thenAccept(modelList -> {
                    javafx.application.Platform.runLater(() -> {
                        availableModels.clear();
                        availableModels.addAll(modelList);
                        activeModels.set((int) modelList.stream().filter(AIModelDto::isLoaded).count());
                    });
                    logger.info("Refreshed {} available AI models", modelList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh available models", throwable);
                    return null;
                });
    }
    
    /**
     * Load AI model
     */
    public CompletableFuture<AIModelDto> loadModel(String modelId, Map<String, Object> config) {
        logger.info("Loading AI model: {}", modelId);
        
        ModelLoadRequest request = new ModelLoadRequest();
        request.setModelId(modelId);
        request.setConfig(config);
        
        return httpClient.postAiSubsystemAsync("/api/ai/models/load", request, AIModelDto.class)
                .thenApply(model -> {
                    javafx.application.Platform.runLater(() -> {
                        // Update model in list
                        for (int i = 0; i < availableModels.size(); i++) {
                            if (availableModels.get(i).getId().equals(modelId)) {
                                availableModels.set(i, model);
                                break;
                            }
                        }
                        activeModels.set((int) availableModels.stream().filter(AIModelDto::isLoaded).count());
                    });
                    logger.info("AI model loaded successfully: {}", model.getName());
                    return model;
                })
                .exceptionally(throwable -> {
                    logger.error("AI model loading failed: {}", modelId, throwable);
                    throw new RuntimeException("AI model loading failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Unload AI model
     */
    public CompletableFuture<Boolean> unloadModel(String modelId) {
        logger.info("Unloading AI model: {}", modelId);
        
        return httpClient.postAiSubsystemAsync("/api/ai/models/" + modelId + "/unload", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            availableModels.stream()
                                    .filter(model -> model.getId().equals(modelId))
                                    .findFirst()
                                    .ifPresent(model -> model.setLoaded(false));
                            activeModels.set((int) availableModels.stream().filter(AIModelDto::isLoaded).count());
                        });
                        logger.info("AI model unloaded successfully: {}", modelId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("AI model unloading failed: {}", modelId, throwable);
                    return false;
                });
    }
    
    // ============================================
    // PREDICTION & INFERENCE
    // ============================================
    
    /**
     * Make prediction
     */
    public CompletableFuture<PredictionResultDto> makePrediction(String modelId, Map<String, Object> inputData) {
        logger.info("Making prediction with model: {}", modelId);
        
        PredictionRequest request = new PredictionRequest();
        request.setModelId(modelId);
        request.setInputData(inputData);
        
        return httpClient.postAiSubsystemAsync("/api/ai/predict", request, PredictionResultDto.class)
                .thenApply(result -> {
                    logger.info("Prediction completed with confidence: {}", result.getConfidence());
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("Prediction failed for model: {}", modelId, throwable);
                    throw new RuntimeException("Prediction failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Batch prediction
     */
    public CompletableFuture<List<PredictionResultDto>> makeBatchPrediction(String modelId, List<Map<String, Object>> inputDataList) {
        logger.info("Making batch prediction with model: {} for {} inputs", modelId, inputDataList.size());
        
        BatchPredictionRequest request = new BatchPredictionRequest();
        request.setModelId(modelId);
        request.setInputDataList(inputDataList);
        
        return httpClient.postAiSubsystemAsync("/api/ai/predict/batch", request, new TypeReference<List<PredictionResultDto>>() {})
                .thenApply(results -> {
                    logger.info("Batch prediction completed - {} results generated", results.size());
                    return results;
                })
                .exceptionally(throwable -> {
                    logger.error("Batch prediction failed for model: {}", modelId, throwable);
                    throw new RuntimeException("Batch prediction failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Get model predictions history
     */
    public CompletableFuture<List<PredictionHistoryDto>> getPredictionHistory(String modelId, String timeRange) {
        return httpClient.getAiSubsystemAsync("/api/ai/models/" + modelId + "/predictions?timeRange=" + timeRange, 
                new TypeReference<List<PredictionHistoryDto>>() {})
                .exceptionally(throwable -> {
                    logger.error("Failed to get prediction history: {}", modelId, throwable);
                    return List.of();
                });
    }
    
    // ============================================
    // TRAINING & LEARNING
    // ============================================
    
    /**
     * Get training jobs
     */
    public ObservableList<TrainingJobDto> getTrainingJobs() {
        return trainingJobs;
    }
    
    /**
     * Start model training
     */
    public CompletableFuture<TrainingJobDto> startTraining(String modelType, String datasetId, Map<String, Object> trainingParams) {
        logger.info("Starting training for model type: {} with dataset: {}", modelType, datasetId);
        
        TrainingStartRequest request = new TrainingStartRequest();
        request.setModelType(modelType);
        request.setDatasetId(datasetId);
        request.setTrainingParams(trainingParams);
        
        return httpClient.postAiSubsystemAsync("/api/ai/training/start", request, TrainingJobDto.class)
                .thenApply(job -> {
                    javafx.application.Platform.runLater(() -> {
                        trainingJobs.add(job);
                        runningJobs.set((int) trainingJobs.stream().filter(TrainingJobDto::isActive).count());
                    });
                    logger.info("Training job started successfully: {}", job.getId());
                    return job;
                })
                .exceptionally(throwable -> {
                    logger.error("Training start failed for model type: {}", modelType, throwable);
                    throw new RuntimeException("Training start failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Stop training job
     */
    public CompletableFuture<Boolean> stopTraining(String jobId) {
        logger.info("Stopping training job: {}", jobId);
        
        return httpClient.postAiSubsystemAsync("/api/ai/training/" + jobId + "/stop", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            trainingJobs.stream()
                                    .filter(job -> job.getId().equals(jobId))
                                    .findFirst()
                                    .ifPresent(job -> job.setActive(false));
                            runningJobs.set((int) trainingJobs.stream().filter(TrainingJobDto::isActive).count());
                        });
                        logger.info("Training job stopped successfully: {}", jobId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Training stop failed: {}", jobId, throwable);
                    return false;
                });
    }
    
    /**
     * Get training job status
     */
    public CompletableFuture<TrainingJobDto> getTrainingJobStatus(String jobId) {
        return httpClient.getAiSubsystemAsync("/api/ai/training/" + jobId + "/status", TrainingJobDto.class)
                .thenApply(job -> {
                    javafx.application.Platform.runLater(() -> {
                        // Update job in list
                        for (int i = 0; i < trainingJobs.size(); i++) {
                            if (trainingJobs.get(i).getId().equals(jobId)) {
                                trainingJobs.set(i, job);
                                break;
                            }
                        }
                        runningJobs.set((int) trainingJobs.stream().filter(TrainingJobDto::isActive).count());
                    });
                    return job;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get training job status: {}", jobId, throwable);
                    return new TrainingJobDto();
                });
    }
    
    // ============================================
    // INTELLIGENT INSIGHTS
    // ============================================
    
    /**
     * Get data insights
     */
    public CompletableFuture<List<AIInsightDto>> getDataInsights(String datasetId, List<String> analysisTypes) {
        logger.info("Getting AI insights for dataset: {}", datasetId);
        
        InsightsRequest request = new InsightsRequest();
        request.setDatasetId(datasetId);
        request.setAnalysisTypes(analysisTypes);
        
        return httpClient.postAiSubsystemAsync("/api/ai/insights/data", request, new TypeReference<List<AIInsightDto>>() {})
                .thenApply(insights -> {
                    logger.info("Generated {} AI insights for dataset: {}", insights.size(), datasetId);
                    return insights;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get data insights: {}", datasetId, throwable);
                    return List.of();
                });
    }
    
    /**
     * Get system performance insights
     */
    public CompletableFuture<List<AIInsightDto>> getPerformanceInsights(String timeRange) {
        return httpClient.getAiSubsystemAsync("/api/ai/insights/performance?timeRange=" + timeRange, 
                new TypeReference<List<AIInsightDto>>() {})
                .thenApply(insights -> {
                    logger.info("Generated {} performance insights", insights.size());
                    return insights;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get performance insights", throwable);
                    return List.of();
                });
    }
    
    /**
     * Get security insights
     */
    public CompletableFuture<List<AIInsightDto>> getSecurityInsights() {
        return httpClient.getAiSubsystemAsync("/api/ai/insights/security", new TypeReference<List<AIInsightDto>>() {})
                .thenApply(insights -> {
                    logger.info("Generated {} security insights", insights.size());
                    return insights;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get security insights", throwable);
                    return List.of();
                });
    }
    
    // ============================================
    // AUTO-LEARNING
    // ============================================
    
    /**
     * Enable auto-learning
     */
    public CompletableFuture<Boolean> enableAutoLearning(Map<String, Object> config) {
        logger.info("Enabling auto-learning");
        
        return httpClient.postAiSubsystemAsync("/api/ai/auto-learning/enable", config, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            autoLearningEnabled.set(true);
                        });
                        logger.info("Auto-learning enabled successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to enable auto-learning", throwable);
                    return false;
                });
    }
    
    /**
     * Disable auto-learning
     */
    public CompletableFuture<Boolean> disableAutoLearning() {
        logger.info("Disabling auto-learning");
        
        return httpClient.postAiSubsystemAsync("/api/ai/auto-learning/disable", null, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            autoLearningEnabled.set(false);
                        });
                        logger.info("Auto-learning disabled successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to disable auto-learning", throwable);
                    return false;
                });
    }
    
    /**
     * Get auto-learning status
     */
    public CompletableFuture<AutoLearningStatusDto> getAutoLearningStatus() {
        return httpClient.getAiSubsystemAsync("/api/ai/auto-learning/status", AutoLearningStatusDto.class)
                .thenApply(status -> {
                    javafx.application.Platform.runLater(() -> {
                        autoLearningEnabled.set(status.isEnabled());
                    });
                    return status;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get auto-learning status", throwable);
                    return new AutoLearningStatusDto();
                });
    }
    
    // ============================================
    // MODEL OPTIMIZATION
    // ============================================
    
    /**
     * Optimize model
     */
    public CompletableFuture<ModelOptimizationResultDto> optimizeModel(String modelId, List<String> optimizationTypes) {
        logger.info("Optimizing model: {} with types: {}", modelId, optimizationTypes);
        
        ModelOptimizationRequest request = new ModelOptimizationRequest();
        request.setModelId(modelId);
        request.setOptimizationTypes(optimizationTypes);
        
        return httpClient.postAiSubsystemAsync("/api/ai/models/optimize", request, ModelOptimizationResultDto.class)
                .thenApply(result -> {
                    logger.info("Model optimization completed - Performance improvement: {}%", result.getPerformanceImprovement());
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("Model optimization failed: {}", modelId, throwable);
                    throw new RuntimeException("Model optimization failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * A/B test models
     */
    public CompletableFuture<ABTestResultDto> runABTest(String modelAId, String modelBId, List<Map<String, Object>> testData) {
        logger.info("Running A/B test between models: {} vs {}", modelAId, modelBId);
        
        ABTestRequest request = new ABTestRequest();
        request.setModelAId(modelAId);
        request.setModelBId(modelBId);
        request.setTestData(testData);
        
        return httpClient.postAiSubsystemAsync("/api/ai/testing/ab-test", request, ABTestResultDto.class)
                .thenApply(result -> {
                    logger.info("A/B test completed - Winner: {}", result.getWinnerModelId());
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("A/B test failed: {} vs {}", modelAId, modelBId, throwable);
                    throw new RuntimeException("A/B test failed: " + throwable.getMessage(), throwable);
                });
    }
    
    // ============================================
    // PROPERTY ACCESSORS
    // ============================================
    
    public StringProperty aiSystemStatusProperty() { return aiSystemStatus; }
    public String getAiSystemStatus() { return aiSystemStatus.get(); }
    
    public IntegerProperty activeModelsProperty() { return activeModels; }
    public int getActiveModels() { return activeModels.get(); }
    
    public IntegerProperty runningJobsProperty() { return runningJobs; }
    public int getRunningJobs() { return runningJobs.get(); }
    
    public DoubleProperty systemLoadProperty() { return systemLoad; }
    public double getSystemLoad() { return systemLoad.get(); }
    
    public BooleanProperty autoLearningEnabledProperty() { return autoLearningEnabled; }
    public boolean isAutoLearningEnabled() { return autoLearningEnabled.get(); }
    
    // ============================================
    // DTO CLASSES
    // ============================================
    
    public static class AISystemHealthDto {
        private String status;
        private double systemLoad;
        private int activeModels;
        private String message;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public double getSystemLoad() { return systemLoad; }
        public void setSystemLoad(double systemLoad) { this.systemLoad = systemLoad; }
        
        public int getActiveModels() { return activeModels; }
        public void setActiveModels(int activeModels) { this.activeModels = activeModels; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    private static class ModelLoadRequest {
        private String modelId;
        private Map<String, Object> config;
        
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        
        public Map<String, Object> getConfig() { return config; }
        public void setConfig(Map<String, Object> config) { this.config = config; }
    }
    
    private static class PredictionRequest {
        private String modelId;
        private Map<String, Object> inputData;
        
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        
        public Map<String, Object> getInputData() { return inputData; }
        public void setInputData(Map<String, Object> inputData) { this.inputData = inputData; }
    }
    
    private static class BatchPredictionRequest {
        private String modelId;
        private List<Map<String, Object>> inputDataList;
        
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        
        public List<Map<String, Object>> getInputDataList() { return inputDataList; }
        public void setInputDataList(List<Map<String, Object>> inputDataList) { this.inputDataList = inputDataList; }
    }
    
    private static class TrainingStartRequest {
        private String modelType;
        private String datasetId;
        private Map<String, Object> trainingParams;
        
        public String getModelType() { return modelType; }
        public void setModelType(String modelType) { this.modelType = modelType; }
        
        public String getDatasetId() { return datasetId; }
        public void setDatasetId(String datasetId) { this.datasetId = datasetId; }
        
        public Map<String, Object> getTrainingParams() { return trainingParams; }
        public void setTrainingParams(Map<String, Object> trainingParams) { this.trainingParams = trainingParams; }
    }
    
    private static class InsightsRequest {
        private String datasetId;
        private List<String> analysisTypes;
        
        public String getDatasetId() { return datasetId; }
        public void setDatasetId(String datasetId) { this.datasetId = datasetId; }
        
        public List<String> getAnalysisTypes() { return analysisTypes; }
        public void setAnalysisTypes(List<String> analysisTypes) { this.analysisTypes = analysisTypes; }
    }
    
    private static class ModelOptimizationRequest {
        private String modelId;
        private List<String> optimizationTypes;
        
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        
        public List<String> getOptimizationTypes() { return optimizationTypes; }
        public void setOptimizationTypes(List<String> optimizationTypes) { this.optimizationTypes = optimizationTypes; }
    }
    
    private static class ABTestRequest {
        private String modelAId;
        private String modelBId;
        private List<Map<String, Object>> testData;
        
        public String getModelAId() { return modelAId; }
        public void setModelAId(String modelAId) { this.modelAId = modelAId; }
        
        public String getModelBId() { return modelBId; }
        public void setModelBId(String modelBId) { this.modelBId = modelBId; }
        
        public List<Map<String, Object>> getTestData() { return testData; }
        public void setTestData(List<Map<String, Object>> testData) { this.testData = testData; }
    }
    
    public static class PredictionHistoryDto {
        private String id;
        private String timestamp;
        private Map<String, Object> input;
        private Object result;
        private double confidence;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        
        public Map<String, Object> getInput() { return input; }
        public void setInput(Map<String, Object> input) { this.input = input; }
        
        public Object getResult() { return result; }
        public void setResult(Object result) { this.result = result; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }
    
    public static class AutoLearningStatusDto {
        private boolean enabled;
        private String mode;
        private int modelsLearning;
        private String lastUpdate;
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        
        public int getModelsLearning() { return modelsLearning; }
        public void setModelsLearning(int modelsLearning) { this.modelsLearning = modelsLearning; }
        
        public String getLastUpdate() { return lastUpdate; }
        public void setLastUpdate(String lastUpdate) { this.lastUpdate = lastUpdate; }
    }
    
    public static class ModelOptimizationResultDto {
        private String modelId;
        private double performanceImprovement;
        private double sizReduction;
        private List<String> optimizationsApplied;
        
        public String getModelId() { return modelId; }
        public void setModelId(String modelId) { this.modelId = modelId; }
        
        public double getPerformanceImprovement() { return performanceImprovement; }
        public void setPerformanceImprovement(double performanceImprovement) { this.performanceImprovement = performanceImprovement; }
        
        public double getSizReduction() { return sizReduction; }
        public void setSizReduction(double sizReduction) { this.sizReduction = sizReduction; }
        
        public List<String> getOptimizationsApplied() { return optimizationsApplied; }
        public void setOptimizationsApplied(List<String> optimizationsApplied) { this.optimizationsApplied = optimizationsApplied; }
    }
    
    public static class ABTestResultDto {
        private String winnerModelId;
        private double modelAPerformance;
        private double modelBPerformance;
        private double confidenceLevel;
        private String metrics;
        
        public String getWinnerModelId() { return winnerModelId; }
        public void setWinnerModelId(String winnerModelId) { this.winnerModelId = winnerModelId; }
        
        public double getModelAPerformance() { return modelAPerformance; }
        public void setModelAPerformance(double modelAPerformance) { this.modelAPerformance = modelAPerformance; }
        
        public double getModelBPerformance() { return modelBPerformance; }
        public void setModelBPerformance(double modelBPerformance) { this.modelBPerformance = modelBPerformance; }
        
        public double getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
        
        public String getMetrics() { return metrics; }
        public void setMetrics(String metrics) { this.metrics = metrics; }
    }
}