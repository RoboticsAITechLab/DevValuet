package com.devvault.cockpit.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/devvault/cockpit/ui/main.fxml"));
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/com/devvault/cockpit/ui/style.css").toExternalForm());
        primaryStage.setTitle("DevValuet Cockpit — 2100");
        primaryStage.setScene(scene);
        primaryStage.show();

        // small startup health check against local cockpit-core
        try {
            HttpClient c = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
            HttpRequest req = HttpRequest.newBuilder().uri(new URI("http://localhost:8085/health")).timeout(Duration.ofSeconds(2)).GET().build();
            c.sendAsync(req, HttpResponse.BodyHandlers.ofString()).thenAccept(resp -> {
                try {
                    Label lbl = (Label) scene.lookup(".title");
                    if (lbl != null) {
                        lbl.setText(lbl.getText() + " — Core: " + resp.statusCode());
                    }
                } catch (Exception ignore) {}
            }).exceptionally(ex -> { return null; });
        } catch (Exception ignore) {}
    }

    public static void main(String[] args) {
        launch(args);
    }
}
