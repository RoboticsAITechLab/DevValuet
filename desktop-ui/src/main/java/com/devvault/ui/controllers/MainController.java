package com.devvault.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.time.Instant;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.application.Platform;
import java.util.Random;

public class MainController {
    @FXML
    private Label statusLabel;
    @FXML
    private Button refreshBtn;
    @FXML
    private Label telemetryLabel;

    @FXML
    public void initialize() {
        refreshHealth();
        refreshBtn.setOnAction(e -> refreshHealth());

        // Demo telemetry updater (mocked values) to make UI feel dynamic
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(1), ev -> updateTelemetry()));
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
    }

    private final Random rand = new Random();

    private void updateTelemetry() {
        int cpu = 20 + rand.nextInt(60);
        int gpu = 5 + rand.nextInt(50);
        int ram = 30 + rand.nextInt(60);
        Platform.runLater(() -> telemetryLabel.setText(String.format("CPU: %d%%  GPU: %d%%  RAM: %d%%", cpu, gpu, ram)));
    }

    private void refreshHealth() {
        try {
            java.net.URI uri = java.net.URI.create("http://localhost:8080/health");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        String body = response.body();
                        Platform.runLater(() -> statusLabel.setText("Backend: " + body));
                    })
                    .exceptionally(ex -> {
                        Platform.runLater(() -> statusLabel.setText("Backend: offline (" + Instant.now().toString() + ")"));
                        return null;
                    });
        } catch (Exception ex) {
            statusLabel.setText("Backend: offline (" + Instant.now().toString() + ")");
        }
    }
}
