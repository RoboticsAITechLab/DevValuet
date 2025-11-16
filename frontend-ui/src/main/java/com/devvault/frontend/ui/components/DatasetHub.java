package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.util.logging.Logger;

/**
 * Dataset Hub - Data management interface
 */
public final class DatasetHub extends VBox {
    private static final Logger logger = Logger.getLogger(DatasetHub.class.getName());

    public DatasetHub() {
        super(15);
        logger.info("Creating DatasetHub...");
        
        setPadding(new Insets(20));
        getStyleClass().add("dataset-hub");
        
        try {
            initializeComponents();
            logger.info("DatasetHub initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize DatasetHub: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        var header = new Label("Dataset Hub");
        header.getStyleClass().addAll("view-header", "large-text");
        
        var datasetList = createDatasetList();
        var actionsPanel = createActionsPanel();
        
        getChildren().addAll(header, datasetList, actionsPanel);
    }
    
    private VBox createDatasetList() {
        var container = new VBox(10);
        container.getStyleClass().add("dataset-list-container");
        
        var titleLabel = new Label("Available Datasets");
        titleLabel.getStyleClass().addAll("section-title", "medium-text");
        
        var datasetList = new ListView<String>();
        datasetList.getStyleClass().add("dataset-list");
        datasetList.setPrefHeight(300);
        
        datasetList.getItems().addAll(
            "Customer Analytics Q4-2024 (1.2GB)",
            "User Behavior Tracking (850MB)",
            "Sales Performance Data (450MB)",
            "Product Feedback Dataset (320MB)"
        );
        
        container.getChildren().addAll(titleLabel, datasetList);
        return container;
    }
    
    private HBox createActionsPanel() {
        var actionsPanel = new HBox(10);
        actionsPanel.getStyleClass().add("dataset-actions");
        
        var uploadBtn = new Button("Upload Dataset");
        uploadBtn.getStyleClass().addAll("action-button", "upload-button");
        uploadBtn.setOnAction(e -> uploadDataset());
        
        var importBtn = new Button("Import External");
        importBtn.getStyleClass().addAll("action-button", "import-button");
        importBtn.setOnAction(e -> importDataset());
        
        var analyzeBtn = new Button("Analyze Selected");
        analyzeBtn.getStyleClass().addAll("action-button", "analyze-button");
        analyzeBtn.setOnAction(e -> analyzeDataset());
        
        actionsPanel.getChildren().addAll(uploadBtn, importBtn, analyzeBtn);
        return actionsPanel;
    }
    
    private void uploadDataset() {
        showAlert("Upload", "Dataset upload functionality available.");
    }
    
    private void importDataset() {
        showAlert("Import", "External dataset import initiated.");
    }
    
    private void analyzeDataset() {
        showAlert("Analysis", "Dataset analysis started.");
    }
    
    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}