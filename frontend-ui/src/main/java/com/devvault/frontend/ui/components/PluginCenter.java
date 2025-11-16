package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.util.logging.Logger;

/**
 * Plugin Center - Plugin management interface
 */
public final class PluginCenter extends VBox {
    private static final Logger logger = Logger.getLogger(PluginCenter.class.getName());

    public PluginCenter() {
        super(15);
        logger.info("Creating PluginCenter...");
        
        setPadding(new Insets(20));
        getStyleClass().add("plugin-center");
        
        try {
            initializeComponents();
            logger.info("PluginCenter initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize PluginCenter: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        var header = new Label("Plugin Center");
        header.getStyleClass().addAll("view-header", "large-text");
        
        var pluginList = createPluginList();
        var actionsPanel = createActionsPanel();
        
        getChildren().addAll(header, pluginList, actionsPanel);
    }
    
    private VBox createPluginList() {
        var container = new VBox(10);
        container.getStyleClass().add("plugin-list-container");
        
        var titleLabel = new Label("Installed Plugins");
        titleLabel.getStyleClass().addAll("section-title", "medium-text");
        
        var pluginList = new ListView<String>();
        pluginList.getStyleClass().add("plugin-list");
        pluginList.setPrefHeight(300);
        
        pluginList.getItems().addAll(
            "Code Formatter Pro v2.1 (Active)",
            "Deployment Manager v1.8 (Active)",
            "Security Scanner v3.2 (Active)",
            "Performance Monitor v1.5 (Inactive)"
        );
        
        container.getChildren().addAll(titleLabel, pluginList);
        return container;
    }
    
    private HBox createActionsPanel() {
        var actionsPanel = new HBox(10);
        actionsPanel.getStyleClass().add("plugin-actions");
        
        var installBtn = new Button("Install Plugin");
        installBtn.getStyleClass().addAll("action-button", "install-button");
        installBtn.setOnAction(e -> installPlugin());
        
        var updateBtn = new Button("Check Updates");
        updateBtn.getStyleClass().addAll("action-button", "update-button");
        updateBtn.setOnAction(e -> checkUpdates());
        
        var manageBtn = new Button("Manage Plugins");
        manageBtn.getStyleClass().addAll("action-button", "manage-button");
        manageBtn.setOnAction(e -> managePlugins());
        
        actionsPanel.getChildren().addAll(installBtn, updateBtn, manageBtn);
        return actionsPanel;
    }
    
    private void installPlugin() {
        showAlert("Install", "Plugin installation interface available.");
    }
    
    private void checkUpdates() {
        showAlert("Updates", "Checking for plugin updates...");
    }
    
    private void managePlugins() {
        showAlert("Management", "Plugin management interface opened.");
    }
    
    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}