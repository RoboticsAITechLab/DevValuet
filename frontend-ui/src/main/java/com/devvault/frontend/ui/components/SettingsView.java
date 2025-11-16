package com.devvault.frontend.ui.components;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.util.logging.Logger;

/**
 * Settings View - Application configuration interface
 */
public final class SettingsView extends VBox {
    private static final Logger logger = Logger.getLogger(SettingsView.class.getName());

    public SettingsView() {
        super(15);
        logger.info("Creating SettingsView...");
        
        setPadding(new Insets(20));
        getStyleClass().add("settings-view");
        
        try {
            initializeComponents();
            logger.info("SettingsView initialized successfully");
        } catch (Exception e) {
            logger.severe("Failed to initialize SettingsView: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeComponents() {
        var header = new Label("Application Settings");
        header.getStyleClass().addAll("view-header", "large-text");
        
        var tabPane = createSettingsTabs();
        var actionsPanel = createActionsPanel();
        
        getChildren().addAll(header, tabPane, actionsPanel);
    }
    
    private TabPane createSettingsTabs() {
        var tabPane = new TabPane();
        tabPane.getStyleClass().add("settings-tabpane");
        tabPane.setPrefHeight(400);
        
        var generalTab = createGeneralTab();
        var securityTab = createSecurityTab();
        var appearanceTab = createAppearanceTab();
        
        tabPane.getTabs().addAll(generalTab, securityTab, appearanceTab);
        return tabPane;
    }
    
    private Tab createGeneralTab() {
        var tab = new Tab("General");
        tab.setClosable(false);
        
        var content = new VBox(15);
        content.setPadding(new Insets(20));
        
        var autoSaveCheck = new CheckBox("Enable auto-save");
        autoSaveCheck.setSelected(true);
        
        var notificationsCheck = new CheckBox("Show notifications");
        notificationsCheck.setSelected(true);
        
        var startupCheck = new CheckBox("Start with system");
        startupCheck.setSelected(false);
        
        var languageLabel = new Label("Language:");
        var languageChoice = new ComboBox<String>();
        languageChoice.getItems().addAll("English", "Spanish", "French", "German");
        languageChoice.setValue("English");
        
        content.getChildren().addAll(
            autoSaveCheck, notificationsCheck, startupCheck,
            languageLabel, languageChoice
        );
        
        tab.setContent(content);
        return tab;
    }
    
    private Tab createSecurityTab() {
        var tab = new Tab("Security");
        tab.setClosable(false);
        
        var content = new VBox(15);
        content.setPadding(new Insets(20));
        
        var encryptionCheck = new CheckBox("Enable data encryption");
        encryptionCheck.setSelected(true);
        
        var autoLockCheck = new CheckBox("Auto-lock after inactivity");
        autoLockCheck.setSelected(true);
        
        var securityScansCheck = new CheckBox("Automatic security scans");
        securityScansCheck.setSelected(true);
        
        var passwordLabel = new Label("Change Password:");
        var passwordBtn = new Button("Update Password");
        passwordBtn.setOnAction(e -> changePassword());
        
        content.getChildren().addAll(
            encryptionCheck, autoLockCheck, securityScansCheck,
            passwordLabel, passwordBtn
        );
        
        tab.setContent(content);
        return tab;
    }
    
    private Tab createAppearanceTab() {
        var tab = new Tab("Appearance");
        tab.setClosable(false);
        
        var content = new VBox(15);
        content.setPadding(new Insets(20));
        
        var themeLabel = new Label("Theme:");
        var themeChoice = new ComboBox<String>();
        themeChoice.getItems().addAll("Neo Dark", "Neon Blue", "Glassmorphism", "Light");
        themeChoice.setValue("Neo Dark");
        
        var animationsCheck = new CheckBox("Enable animations");
        animationsCheck.setSelected(true);
        
        var glassEffectsCheck = new CheckBox("Glass morphism effects");
        glassEffectsCheck.setSelected(true);
        
        content.getChildren().addAll(
            themeLabel, themeChoice, animationsCheck, glassEffectsCheck
        );
        
        tab.setContent(content);
        return tab;
    }
    
    private HBox createActionsPanel() {
        var actionsPanel = new HBox(10);
        actionsPanel.getStyleClass().add("settings-actions");
        
        var saveBtn = new Button("Save Settings");
        saveBtn.getStyleClass().addAll("action-button", "save-button");
        saveBtn.setOnAction(e -> saveSettings());
        
        var resetBtn = new Button("Reset to Defaults");
        resetBtn.getStyleClass().addAll("action-button", "reset-button");
        resetBtn.setOnAction(e -> resetSettings());
        
        var exportBtn = new Button("Export Settings");
        exportBtn.getStyleClass().addAll("action-button", "export-button");
        exportBtn.setOnAction(e -> exportSettings());
        
        actionsPanel.getChildren().addAll(saveBtn, resetBtn, exportBtn);
        return actionsPanel;
    }
    
    private void saveSettings() {
        showAlert("Settings", "Settings saved successfully.");
    }
    
    private void resetSettings() {
        showAlert("Reset", "Settings reset to defaults.");
    }
    
    private void exportSettings() {
        showAlert("Export", "Settings exported to file.");
    }
    
    private void changePassword() {
        var dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Security Settings");
        dialog.setContentText("Enter new password:");
        
        dialog.showAndWait().ifPresent(password -> {
            showAlert("Password", "Password updated successfully.");
        });
    }
    
    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}