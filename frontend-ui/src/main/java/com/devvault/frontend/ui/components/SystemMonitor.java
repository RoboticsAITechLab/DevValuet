package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.logging.Logger;

/**
 * System Monitor - Real-time system monitoring
 */
public final class SystemMonitor extends VBox {
    private static final Logger logger = Logger.getLogger(SystemMonitor.class.getName());

    public SystemMonitor() {
        super(15);
        logger.info("Creating SystemMonitor...");
        
        setPadding(new Insets(20));
        getStyleClass().add("system-monitor");
        
        try {
            initializeComponents();
            logger.info("SystemMonitor initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize SystemMonitor: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        var header = new Label("System Monitor");
        header.getStyleClass().addAll("view-header", "large-text");
        
        var metricsPanel = createMetricsPanel();
        var chartPanel = createChartPanel();
        
        getChildren().addAll(header, metricsPanel, chartPanel);
    }
    
    private HBox createMetricsPanel() {
        var container = new HBox(20);
        container.getStyleClass().add("metrics-panel");
        container.setPadding(new Insets(10));
        
        var cpuCard = createMetricCard("CPU Usage", "45%", "cpu-metric");
        var memoryCard = createMetricCard("Memory", "2.1GB/8GB", "memory-metric");
        var diskCard = createMetricCard("Disk I/O", "125MB/s", "disk-metric");
        var networkCard = createMetricCard("Network", "1.2Mbps", "network-metric");
        
        container.getChildren().addAll(cpuCard, memoryCard, diskCard, networkCard);
        return container;
    }
    
    private VBox createMetricCard(String title, String value, String styleClass) {
        var card = new VBox(5);
        card.getStyleClass().addAll("metric-card", styleClass);
        card.setPadding(new Insets(15));
        card.setPrefSize(180, 80);
        
        var titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        
        var valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll("metric-value", "medium-text");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createChartPanel() {
        var container = new VBox(10);
        container.getStyleClass().add("chart-panel");
        
        var titleLabel = new Label("Real-time Performance");
        titleLabel.getStyleClass().addAll("section-title", "medium-text");
        
        // CPU chart
        var xAxis = new NumberAxis();
        var yAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Usage %");
        
        var lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("CPU Usage Over Time");
        lineChart.setPrefHeight(250);
        
        var series = new XYChart.Series<Number, Number>();
        series.setName("CPU");
        
        for (int i = 0; i < 60; i++) {
            series.getData().add(new XYChart.Data<>(i, 20 + Math.random() * 60));
        }
        
        lineChart.getData().add(series);
        
        container.getChildren().addAll(titleLabel, lineChart);
        return container;
    }
}