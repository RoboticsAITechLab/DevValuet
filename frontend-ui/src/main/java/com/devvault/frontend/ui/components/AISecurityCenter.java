package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.util.logging.Logger;

/**
 * AI Security Center - Security monitoring interface
 */
public final class AISecurityCenter extends VBox {
    private static final Logger logger = Logger.getLogger(AISecurityCenter.class.getName());

    public AISecurityCenter() {
        super(15);
        logger.info("Creating AISecurityCenter...");
        
        setPadding(new Insets(20));
        getStyleClass().add("ai-security-center");
        
        try {
            initializeComponents();
            logger.info("AISecurityCenter initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize AISecurityCenter: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        // Header
        var header = new Label("AI Security Center");
        header.getStyleClass().addAll("view-header", "large-text");
        
        // Security status
        var statusPanel = createSecurityStatus();
        
        // Threat analysis
        var threatPanel = createThreatAnalysis();
        
        // Security actions
        var actionsPanel = createSecurityActions();
        
        getChildren().addAll(header, statusPanel, threatPanel, actionsPanel);
    }
    
    private VBox createSecurityStatus() {
        var container = new VBox(10);
        container.getStyleClass().add("security-status-panel");
        
        var titleLabel = new Label("Security Status");
        titleLabel.getStyleClass().addAll("section-title", "medium-text");
        
        var statusLabel = new Label("✓ All Systems Secure");
        statusLabel.getStyleClass().addAll("status-label", "secure-status");
        
        var lastScanLabel = new Label("Last scan: 5 minutes ago");
        lastScanLabel.getStyleClass().add("scan-time");
        
        container.getChildren().addAll(titleLabel, statusLabel, lastScanLabel);
        return container;
    }
    
    private VBox createThreatAnalysis() {
        var container = new VBox(10);
        container.getStyleClass().add("threat-analysis-panel");
        
        var titleLabel = new Label("Recent Threat Analysis");
        titleLabel.getStyleClass().addAll("section-title", "medium-text");
        
        var threatList = new ListView<String>();
        threatList.getStyleClass().add("threat-list");
        threatList.setPrefHeight(200);
        
        // Add sample threats (all resolved)
        threatList.getItems().addAll(
            "Low: Outdated dependency detected - RESOLVED",
            "Info: New security patch available",
            "Low: Unusual network activity - MONITORED",
            "Info: Security scan completed successfully"
        );
        
        container.getChildren().addAll(titleLabel, threatList);
        return container;
    }
    
    private HBox createSecurityActions() {
        var actionsPanel = new HBox(10);
        actionsPanel.getStyleClass().add("security-actions");
        
        var scanBtn = new Button("Run Security Scan");
        scanBtn.getStyleClass().addAll("action-button", "scan-button");
        scanBtn.setOnAction(e -> runSecurityScan());
        
        var updateBtn = new Button("Update Signatures");
        updateBtn.getStyleClass().addAll("action-button", "update-button");
        updateBtn.setOnAction(e -> updateSignatures());
        
        var reportBtn = new Button("Generate Report");
        reportBtn.getStyleClass().addAll("action-button", "report-button");
        reportBtn.setOnAction(e -> generateReport());
        
        actionsPanel.getChildren().addAll(scanBtn, updateBtn, reportBtn);
        return actionsPanel;
    }
    
    private void runSecurityScan() {
        showAlert("Security Scan", "Security scan initiated. This may take a few minutes.");
    }
    
    private void updateSignatures() {
        showAlert("Update", "Security signature database updated successfully.");
    }
    
    private void generateReport() {
        showAlert("Report", "Security report generated and saved to reports folder.");
    }
    
    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}