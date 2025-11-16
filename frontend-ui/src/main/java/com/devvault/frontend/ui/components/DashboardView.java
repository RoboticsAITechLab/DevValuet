package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.logging.Logger;

/**
 * Dashboard View - Main overview component
 */
public final class DashboardView extends VBox {
    private static final Logger logger = Logger.getLogger(DashboardView.class.getName());

    public DashboardView() {
        super(15);
        logger.info("Creating DashboardView...");
        
        setPadding(new Insets(20));
        getStyleClass().add("dashboard-view");
        
        try {
            initializeComponents();
            logger.info("DashboardView initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize DashboardView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        // Header
        var header = new Label("DevVault Pro X Dashboard");
        header.getStyleClass().addAll("dashboard-header", "large-text");
        
        // Welcome message
        var welcomeLabel = new Label("Welcome to DevVault Pro X Enterprise Suite");
        welcomeLabel.getStyleClass().add("welcome-text");
        
        // Quick stats panel
        var statsPanel = createStatsPanel();
        
        // Recent activity
        var activityPanel = createActivityPanel();
        
        // Performance chart
        var chartPanel = createPerformanceChart();
        
        getChildren().addAll(header, welcomeLabel, statsPanel, activityPanel, chartPanel);
    }
    
    private TilePane createStatsPanel() {
        var statsPanel = new TilePane(10, 10);
        statsPanel.setPrefColumns(4);
        statsPanel.getStyleClass().add("stats-panel");
        
        // Create stat cards
        var cards = java.util.List.of(
            createStatCard("Active Projects", "12", "projects"),
            createStatCard("Total Builds", "347", "builds"),
            createStatCard("Success Rate", "94.2%", "success"),
            createStatCard("System Health", "Good", "health")
        );
        
        statsPanel.getChildren().addAll(cards);
        return statsPanel;
    }
    
    private VBox createStatCard(String title, String value, String type) {
        var card = new VBox(5);
        card.getStyleClass().addAll("metric-card", type + "-card");
        card.setPadding(new Insets(15));
        card.setPrefSize(200, 100);
        
        var titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        
        var valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll("metric-value", "large-text");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private VBox createActivityPanel() {
        var activityPanel = new VBox(10);
        activityPanel.getStyleClass().add("activity-panel");
        activityPanel.setPadding(new Insets(15));
        
        var titleLabel = new Label("Recent Activity");
        titleLabel.getStyleClass().addAll("panel-title", "medium-text");
        
        var activityList = new ListView<String>();
        activityList.getStyleClass().add("activity-list");
        activityList.setPrefHeight(150);
        
        // Add sample activities
        activityList.getItems().addAll(
            "Project 'WebApp-2024' build completed successfully",
            "Security scan finished for 'MobileApp-iOS'",
            "New dataset 'CustomerData-Q4' uploaded",
            "Plugin 'DeploymentManager' installed"
        );
        
        activityPanel.getChildren().addAll(titleLabel, activityList);
        return activityPanel;
    }
    
    private VBox createPerformanceChart() {
        var chartPanel = new VBox(10);
        chartPanel.getStyleClass().add("chart-panel");
        chartPanel.setPadding(new Insets(15));
        
        var titleLabel = new Label("Performance Metrics");
        titleLabel.getStyleClass().addAll("panel-title", "medium-text");
        
        // Create line chart
        var xAxis = new NumberAxis();
        var yAxis = new NumberAxis();
        xAxis.setLabel("Time (hours)");
        yAxis.setLabel("Performance");
        
        var lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("System Performance (Last 24h)");
        lineChart.getStyleClass().add("performance-chart");
        lineChart.setPrefHeight(200);
        
        // Add sample data
        var series = new XYChart.Series<Number, Number>();
        series.setName("CPU Usage");
        
        for (int i = 0; i < 24; i++) {
            series.getData().add(new XYChart.Data<>(i, 30 + Math.random() * 40));
        }
        
        lineChart.getData().add(series);
        
        chartPanel.getChildren().addAll(titleLabel, lineChart);
        return chartPanel;
    }
}