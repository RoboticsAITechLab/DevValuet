package com.devvault.frontend.dto;

public class PredictiveMetrics {
    private double accuracy;
    private double precision;
    private double recall;
    private double f1Score;
    private int predictions;

    public PredictiveMetrics() {}

    public PredictiveMetrics(double accuracy, double precision, double recall, double f1Score, int predictions) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
        this.predictions = predictions;
    }

    // Getters and Setters
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }

    public double getRecall() { return recall; }
    public void setRecall(double recall) { this.recall = recall; }

    public double getF1Score() { return f1Score; }
    public void setF1Score(double f1Score) { this.f1Score = f1Score; }

    public int getPredictions() { return predictions; }
    public void setPredictions(int predictions) { this.predictions = predictions; }
}