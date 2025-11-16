package com.devvault.frontend.dto;

public class AIInsights {
    private int totalInsights;
    private int criticalInsights;
    private int actionableInsights;
    private double averageConfidence;

    public AIInsights() {}

    public AIInsights(int totalInsights, int criticalInsights, int actionableInsights, double averageConfidence) {
        this.totalInsights = totalInsights;
        this.criticalInsights = criticalInsights;
        this.actionableInsights = actionableInsights;
        this.averageConfidence = averageConfidence;
    }

    // Getters and Setters
    public int getTotalInsights() { return totalInsights; }
    public void setTotalInsights(int totalInsights) { this.totalInsights = totalInsights; }

    public int getCriticalInsights() { return criticalInsights; }
    public void setCriticalInsights(int criticalInsights) { this.criticalInsights = criticalInsights; }

    public int getActionableInsights() { return actionableInsights; }
    public void setActionableInsights(int actionableInsights) { this.actionableInsights = actionableInsights; }

    public double getAverageConfidence() { return averageConfidence; }
    public void setAverageConfidence(double averageConfidence) { this.averageConfidence = averageConfidence; }
}