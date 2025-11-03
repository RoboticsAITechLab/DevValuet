package com.devvault.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class MainDashboardController {
    @FXML
    private Pane rootPane;

    @FXML
    private Button backupButton;

    @FXML
    private Label backupStatus;

    // cached manager to avoid re-creating keys / services on every button press
    private com.devvault.common.backup.BackupManagerService backupManager;

    // Direct (pure-desktop) backup: no HTTP client required

    @FXML
    public void initialize() {
        // placeholder: later wire dynamic tiles, realtime updates and actions
        // initialize cached manager here
        try {
            String allowedBase = System.getProperty("user.home") + File.separator + "devvault-backups";
            this.backupManager = new com.devvault.common.backup.BackupManagerService(allowedBase);
        } catch (Exception e) {
            this.backupManager = null; // will initialize lazily
            System.err.println("Warning: failed to initialize BackupManagerService at startup: " + e.getMessage());
        }
    }

    @FXML
    private void onBackupClicked() {
        backupButton.setDisable(true);
        backupStatus.setText("Starting backup...");

        CompletableFuture.runAsync(() -> {
            try {
                // ensure manager is initialized (lazy init if startup failed)
                if (this.backupManager == null) {
                    String allowedBase = System.getProperty("user.home") + File.separator + "devvault-backups";
                    this.backupManager = new com.devvault.common.backup.BackupManagerService(allowedBase);
                }
                java.nio.file.Path sourceDir = Paths.get(System.getProperty("user.dir"));
                java.nio.file.Path created = this.backupManager.createEncryptedBackup(sourceDir, null);
                Platform.runLater(() -> {
                    backupStatus.setText("Backup created: " + created.toAbsolutePath());
                    backupButton.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    backupStatus.setText("Backup failed: " + e.getMessage());
                    backupButton.setDisable(false);
                });
            }
        });
    }
}
