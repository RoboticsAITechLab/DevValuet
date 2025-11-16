package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class PredictionResultDto {
    private String id;
    private String modelId;
    private Map<String, Object> input;
    private Map<String, Object> output;
    private double confidence;
    private LocalDateTime predictedAt;
    private long processingTime;
    private String status;
    private String error;

    // Constructors
    public PredictionResultDto() {}

    public PredictionResultDto(String id, String modelId, Map<String, Object> output, double confidence) {
        this.id = id;
        this.modelId = modelId;
        this.output = output;
        this.confidence = confidence;
        this.predictedAt = LocalDateTime.now();
        this.status = "SUCCESS";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }

    public Map<String, Object> getInput() { return input; }
    public void setInput(Map<String, Object> input) { this.input = input; }

    public Map<String, Object> getOutput() { return output; }
    public void setOutput(Map<String, Object> output) { this.output = output; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public LocalDateTime getPredictedAt() { return predictedAt; }
    public void setPredictedAt(LocalDateTime predictedAt) { this.predictedAt = predictedAt; }

    public long getProcessingTime() { return processingTime; }
    public void setProcessingTime(long processingTime) { this.processingTime = processingTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    @Override
    public String toString() {
        return "PredictionResultDto{" +
                "id='" + id + '\'' +
                ", modelId='" + modelId + '\'' +
                ", confidence=" + confidence +
                ", status='" + status + '\'' +
                ", processingTime=" + processingTime +
                '}';
    }
}