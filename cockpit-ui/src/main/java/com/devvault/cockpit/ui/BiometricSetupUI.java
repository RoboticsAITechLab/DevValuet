package com.devvault.cockpit.ui;

import com.devvault.cockpit.model.BiometricSetupResult;
import com.devvault.cockpit.service.FirstTimeSetupService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.CompletableFuture;

/**
 * UI Component for Biometric Authentication Setup
 * Comprehensive biometric enrollment interface
 */
public class BiometricSetupUI {
    
    private static final Logger logger = LoggerFactory.getLogger(BiometricSetupUI.class);
    
    private FirstTimeSetupService setupService;
    private Stage primaryStage;
    private VBox mainContainer;
    private TabPane biometricTabs;
    private ProgressBar overallProgress;
    private Label statusLabel;
    
    // Face Recognition Components
    private ImageView facePreview;
    private Button captureFaceButton;
    private ProgressIndicator faceProgress;
    private Label faceStatusLabel;
    
    // Fingerprint Components
    private VBox fingerprintContainer;
    private ImageView fingerprintPreview;
    private Button scanFingerprintButton;
    private ProgressIndicator fingerprintProgress;
    private Label fingerprintStatusLabel;
    private ComboBox<String> fingerSelection;
    
    // Voice Recognition Components
    private Button recordVoiceButton;
    private Button playbackButton;
    private ProgressIndicator voiceProgress;
    private Label voiceStatusLabel;
    private Slider volumeIndicator;
    private Label recordingTime;
    
    // Iris Recognition Components
    private ImageView irisPreview;
    private Button scanIrisButton;
    private ProgressIndicator irisProgress;
    private Label irisStatusLabel;
    
    public BiometricSetupUI(FirstTimeSetupService setupService, Stage primaryStage) {
        this.setupService = setupService;
        this.primaryStage = primaryStage;
        initializeComponents();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        // Main container
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #f0f8ff, #e6f3ff);");
        
        // Header
        Label headerLabel = new Label("Biometric Authentication Setup");
        headerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label descriptionLabel = new Label("Set up secure biometric authentication for your DevVault system");
        descriptionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        // Overall progress
        overallProgress = new ProgressBar(0.0);
        overallProgress.setPrefWidth(400);
        overallProgress.setStyle("-fx-accent: #3498db;");
        
        statusLabel = new Label("Ready to begin biometric setup");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        
        // Biometric tabs
        biometricTabs = new TabPane();
        biometricTabs.setPrefSize(800, 600);
        
        // Create tabs for each biometric type
        createFaceRecognitionTab();
        createFingerprintTab();
        createVoiceRecognitionTab();
        createIrisRecognitionTab();
        
        // Navigation buttons
        HBox navigationBox = createNavigationButtons();
        
        // Add all components
        mainContainer.getChildren().addAll(
            headerLabel,
            descriptionLabel,
            new Separator(),
            overallProgress,
            statusLabel,
            biometricTabs,
            navigationBox
        );
    }
    
    private void createFaceRecognitionTab() {
        Tab faceTab = new Tab("Face Recognition");
        faceTab.setClosable(false);
        
        VBox faceContainer = new VBox(15);
        faceContainer.setPadding(new Insets(20));
        faceContainer.setAlignment(Pos.CENTER);
        
        // Face preview area
        facePreview = new ImageView();
        facePreview.setFitWidth(300);
        facePreview.setFitHeight(300);
        facePreview.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2px; -fx-border-radius: 10px;");
        
        // Face capture instructions
        Label instructionsLabel = new Label("Position your face within the frame and click 'Capture Face'");
        instructionsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        
        // Face capture controls
        HBox faceControls = new HBox(10);
        faceControls.setAlignment(Pos.CENTER);
        
        captureFaceButton = new Button("Capture Face");
        captureFaceButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button retakeFaceButton = new Button("Retake");
        retakeFaceButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        faceProgress = new ProgressIndicator();
        faceProgress.setVisible(false);
        faceProgress.setPrefSize(30, 30);
        
        faceControls.getChildren().addAll(captureFaceButton, retakeFaceButton, faceProgress);
        
        // Face status
        faceStatusLabel = new Label("Ready to capture face");
        faceStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Quality indicators
        VBox qualityBox = new VBox(5);
        qualityBox.setAlignment(Pos.CENTER);
        
        Label qualityLabel = new Label("Image Quality Indicators:");
        qualityLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        HBox indicators = new HBox(10);
        indicators.setAlignment(Pos.CENTER);
        
        Circle lightingIndicator = new Circle(8, Color.GRAY);
        Label lightingLabel = new Label("Lighting");
        
        Circle positionIndicator = new Circle(8, Color.GRAY);
        Label positionLabel = new Label("Position");
        
        Circle clarityIndicator = new Circle(8, Color.GRAY);
        Label clarityLabel = new Label("Clarity");
        
        indicators.getChildren().addAll(
            lightingIndicator, lightingLabel,
            positionIndicator, positionLabel,
            clarityIndicator, clarityLabel
        );
        
        qualityBox.getChildren().addAll(qualityLabel, indicators);
        
        faceContainer.getChildren().addAll(
            new Label("Face Recognition Setup"),
            instructionsLabel,
            facePreview,
            faceControls,
            faceStatusLabel,
            qualityBox
        );
        
        faceTab.setContent(faceContainer);
        biometricTabs.getTabs().add(faceTab);
    }
    
    private void createFingerprintTab() {
        Tab fingerprintTab = new Tab("Fingerprint");
        fingerprintTab.setClosable(false);
        
        VBox fingerprintMainContainer = new VBox(15);
        fingerprintMainContainer.setPadding(new Insets(20));
        fingerprintMainContainer.setAlignment(Pos.CENTER);
        
        // Finger selection
        HBox selectionBox = new HBox(10);
        selectionBox.setAlignment(Pos.CENTER);
        
        Label selectLabel = new Label("Select Finger:");
        selectLabel.setStyle("-fx-font-weight: bold;");
        
        fingerSelection = new ComboBox<>();
        fingerSelection.getItems().addAll(
            "Right Thumb", "Right Index", "Right Middle", "Right Ring", "Right Pinky",
            "Left Thumb", "Left Index", "Left Middle", "Left Ring", "Left Pinky"
        );
        fingerSelection.setValue("Right Index");
        
        selectionBox.getChildren().addAll(selectLabel, fingerSelection);
        
        // Fingerprint preview
        fingerprintPreview = new ImageView();
        fingerprintPreview.setFitWidth(200);
        fingerprintPreview.setFitHeight(250);
        fingerprintPreview.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2px; -fx-border-radius: 10px;");
        
        // Instructions
        Label instructionsLabel = new Label("Place your selected finger on the scanner and follow the prompts");
        instructionsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        
        // Fingerprint controls
        HBox fingerprintControls = new HBox(10);
        fingerprintControls.setAlignment(Pos.CENTER);
        
        scanFingerprintButton = new Button("Scan Fingerprint");
        scanFingerprintButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button clearFingerprintButton = new Button("Clear");
        clearFingerprintButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        fingerprintProgress = new ProgressIndicator();
        fingerprintProgress.setVisible(false);
        fingerprintProgress.setPrefSize(30, 30);
        
        fingerprintControls.getChildren().addAll(scanFingerprintButton, clearFingerprintButton, fingerprintProgress);
        
        // Fingerprint status
        fingerprintStatusLabel = new Label("Select a finger and click 'Scan Fingerprint'");
        fingerprintStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Scan progress indicators
        VBox scanProgressBox = new VBox(5);
        scanProgressBox.setAlignment(Pos.CENTER);
        
        Label scanLabel = new Label("Scan Progress:");
        scanLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        ProgressBar scanProgress = new ProgressBar(0.0);
        scanProgress.setPrefWidth(300);
        
        Label scanInstructions = new Label("Keep finger steady during scanning process");
        scanInstructions.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
        
        scanProgressBox.getChildren().addAll(scanLabel, scanProgress, scanInstructions);
        
        fingerprintMainContainer.getChildren().addAll(
            new Label("Fingerprint Setup"),
            selectionBox,
            instructionsLabel,
            fingerprintPreview,
            fingerprintControls,
            fingerprintStatusLabel,
            scanProgressBox
        );
        
        fingerprintTab.setContent(fingerprintMainContainer);
        biometricTabs.getTabs().add(fingerprintTab);
    }
    
    private void createVoiceRecognitionTab() {
        Tab voiceTab = new Tab("Voice Recognition");
        voiceTab.setClosable(false);
        
        VBox voiceContainer = new VBox(15);
        voiceContainer.setPadding(new Insets(20));
        voiceContainer.setAlignment(Pos.CENTER);
        
        // Voice setup instructions
        Label instructionsLabel = new Label("Record your voice saying the passphrase: 'DevVault Secure Access'");
        instructionsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-text-alignment: center;");
        instructionsLabel.setWrapText(true);
        
        // Voice visualization
        Circle micIcon = new Circle(50, Color.LIGHTBLUE);
        Label micLabel = new Label("🎤");
        micLabel.setStyle("-fx-font-size: 30px;");
        
        StackPane micContainer = new StackPane();
        micContainer.getChildren().addAll(micIcon, micLabel);
        
        // Volume level indicator
        volumeIndicator = new Slider(0, 100, 0);
        volumeIndicator.setShowTickMarks(true);
        volumeIndicator.setShowTickLabels(true);
        volumeIndicator.setMajorTickUnit(25);
        volumeIndicator.setPrefWidth(300);
        volumeIndicator.setDisable(true);
        
        Label volumeLabel = new Label("Voice Level");
        volumeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        // Recording controls
        HBox voiceControls = new HBox(10);
        voiceControls.setAlignment(Pos.CENTER);
        
        recordVoiceButton = new Button("Record Voice");
        recordVoiceButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        playbackButton = new Button("Play Back");
        playbackButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        playbackButton.setDisable(true);
        
        voiceProgress = new ProgressIndicator();
        voiceProgress.setVisible(false);
        voiceProgress.setPrefSize(30, 30);
        
        voiceControls.getChildren().addAll(recordVoiceButton, playbackButton, voiceProgress);
        
        // Recording time
        recordingTime = new Label("00:00");
        recordingTime.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Voice status
        voiceStatusLabel = new Label("Ready to record voice sample");
        voiceStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Recording tips
        VBox tipsBox = new VBox(5);
        tipsBox.setAlignment(Pos.CENTER);
        tipsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10; -fx-border-radius: 5px;");
        
        Label tipsLabel = new Label("Recording Tips:");
        tipsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        Label tip1 = new Label("• Speak clearly and at normal volume");
        Label tip2 = new Label("• Record in a quiet environment");
        Label tip3 = new Label("• Say the passphrase 3 times");
        
        tip1.setStyle("-fx-font-size: 11px; -fx-text-fill: #34495e;");
        tip2.setStyle("-fx-font-size: 11px; -fx-text-fill: #34495e;");
        tip3.setStyle("-fx-font-size: 11px; -fx-text-fill: #34495e;");
        
        tipsBox.getChildren().addAll(tipsLabel, tip1, tip2, tip3);
        
        voiceContainer.getChildren().addAll(
            new Label("Voice Recognition Setup"),
            instructionsLabel,
            micContainer,
            volumeLabel,
            volumeIndicator,
            recordingTime,
            voiceControls,
            voiceStatusLabel,
            tipsBox
        );
        
        voiceTab.setContent(voiceContainer);
        biometricTabs.getTabs().add(voiceTab);
    }
    
    private void createIrisRecognitionTab() {
        Tab irisTab = new Tab("Iris Recognition");
        irisTab.setClosable(false);
        
        VBox irisContainer = new VBox(15);
        irisContainer.setPadding(new Insets(20));
        irisContainer.setAlignment(Pos.CENTER);
        
        // Iris setup instructions
        Label instructionsLabel = new Label("Look directly into the camera for iris scanning");
        instructionsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        
        // Iris preview
        irisPreview = new ImageView();
        irisPreview.setFitWidth(250);
        irisPreview.setFitHeight(250);
        irisPreview.setStyle("-fx-border-color: #bdc3c7; -fx-border-width: 2px; -fx-border-radius: 50%;");
        
        // Eye alignment guide
        Circle eyeGuide = new Circle(125, Color.TRANSPARENT);
        eyeGuide.setStroke(Color.DODGERBLUE);
        eyeGuide.setStrokeWidth(3);
        
        StackPane irisGuide = new StackPane();
        irisGuide.getChildren().addAll(irisPreview, eyeGuide);
        
        // Iris controls
        HBox irisControls = new HBox(10);
        irisControls.setAlignment(Pos.CENTER);
        
        scanIrisButton = new Button("Scan Iris");
        scanIrisButton.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        Button calibrateButton = new Button("Calibrate");
        calibrateButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        irisProgress = new ProgressIndicator();
        irisProgress.setVisible(false);
        irisProgress.setPrefSize(30, 30);
        
        irisControls.getChildren().addAll(scanIrisButton, calibrateButton, irisProgress);
        
        // Iris status
        irisStatusLabel = new Label("Position your eye within the guide circle");
        irisStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        // Scanning instructions
        VBox instructionsBox = new VBox(5);
        instructionsBox.setAlignment(Pos.CENTER);
        instructionsBox.setStyle("-fx-background-color: #fff3cd; -fx-padding: 10; -fx-border-radius: 5px;");
        
        Label warningLabel = new Label("⚠️ Important Instructions:");
        warningLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #856404;");
        
        Label inst1 = new Label("• Remove glasses or contacts if possible");
        Label inst2 = new Label("• Keep your eyes wide open");
        Label inst3 = new Label("• Look directly at the camera");
        Label inst4 = new Label("• Hold still during scanning");
        
        inst1.setStyle("-fx-font-size: 11px; -fx-text-fill: #856404;");
        inst2.setStyle("-fx-font-size: 11px; -fx-text-fill: #856404;");
        inst3.setStyle("-fx-font-size: 11px; -fx-text-fill: #856404;");
        inst4.setStyle("-fx-font-size: 11px; -fx-text-fill: #856404;");
        
        instructionsBox.getChildren().addAll(warningLabel, inst1, inst2, inst3, inst4);
        
        irisContainer.getChildren().addAll(
            new Label("Iris Recognition Setup"),
            instructionsLabel,
            irisGuide,
            irisControls,
            irisStatusLabel,
            instructionsBox
        );
        
        irisTab.setContent(irisContainer);
        biometricTabs.getTabs().add(irisTab);
    }
    
    private HBox createNavigationButtons() {
        HBox navigationBox = new HBox(20);
        navigationBox.setAlignment(Pos.CENTER);
        navigationBox.setPadding(new Insets(20));
        
        Button previousButton = new Button("Previous");
        previousButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        Button nextButton = new Button("Next");
        nextButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        Button skipButton = new Button("Skip Biometric Setup");
        skipButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        
        Button completeButton = new Button("Complete Setup");
        completeButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 25;");
        completeButton.setDisable(true);
        
        navigationBox.getChildren().addAll(previousButton, nextButton, skipButton, completeButton);
        
        return navigationBox;
    }
    
    private void setupEventHandlers() {
        // Face capture event
        captureFaceButton.setOnAction(e -> {
            captureFaceButton.setDisable(true);
            faceProgress.setVisible(true);
            faceStatusLabel.setText("Capturing face...");
            
            Task<Void> captureTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // Simulate face capture process
                    Thread.sleep(3000);
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        faceProgress.setVisible(false);
                        captureFaceButton.setDisable(false);
                        faceStatusLabel.setText("Face captured successfully ✓");
                        faceStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
                        updateOverallProgress();
                    });
                }
            };
            
            new Thread(captureTask).start();
        });
        
        // Fingerprint scan event
        scanFingerprintButton.setOnAction(e -> {
            String selectedFinger = fingerSelection.getValue();
            
            scanFingerprintButton.setDisable(true);
            fingerprintProgress.setVisible(true);
            fingerprintStatusLabel.setText("Scanning " + selectedFinger + "...");
            
            Task<Void> scanTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(4000);
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        fingerprintProgress.setVisible(false);
                        scanFingerprintButton.setDisable(false);
                        fingerprintStatusLabel.setText(selectedFinger + " scanned successfully ✓");
                        fingerprintStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
                        updateOverallProgress();
                    });
                }
            };
            
            new Thread(scanTask).start();
        });
        
        // Voice recording event
        recordVoiceButton.setOnAction(e -> {
            if (recordVoiceButton.getText().equals("Record Voice")) {
                startVoiceRecording();
            } else {
                stopVoiceRecording();
            }
        });
        
        // Iris scan event
        scanIrisButton.setOnAction(e -> {
            scanIrisButton.setDisable(true);
            irisProgress.setVisible(true);
            irisStatusLabel.setText("Scanning iris pattern...");
            
            Task<Void> irisTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    Thread.sleep(5000);
                    return null;
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        irisProgress.setVisible(false);
                        scanIrisButton.setDisable(false);
                        irisStatusLabel.setText("Iris pattern captured successfully ✓");
                        irisStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
                        updateOverallProgress();
                    });
                }
            };
            
            new Thread(irisTask).start();
        });
    }
    
    private void startVoiceRecording() {
        recordVoiceButton.setText("Stop Recording");
        recordVoiceButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        voiceStatusLabel.setText("Recording... Speak clearly");
        
        // Simulate recording timer
        Task<Void> recordingTimer = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(1000);
                    final int seconds = i;
                    Platform.runLater(() -> {
                        recordingTime.setText(String.format("00:%02d", seconds));
                        volumeIndicator.setValue(Math.random() * 80 + 20); // Simulate voice level
                    });
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    stopVoiceRecording();
                });
            }
        };
        
        new Thread(recordingTimer).start();
    }
    
    private void stopVoiceRecording() {
        recordVoiceButton.setText("Record Voice");
        recordVoiceButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
        voiceStatusLabel.setText("Voice sample recorded successfully ✓");
        voiceStatusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27ae60;");
        playbackButton.setDisable(false);
        volumeIndicator.setValue(0);
        updateOverallProgress();
    }
    
    private void updateOverallProgress() {
        // Calculate progress based on completed biometric setups
        double progress = 0.0;
        int completedSteps = 0;
        
        // Check each biometric type completion status
        if (faceStatusLabel.getText().contains("successfully")) {
            completedSteps++;
        }
        if (fingerprintStatusLabel.getText().contains("successfully")) {
            completedSteps++;
        }
        if (voiceStatusLabel.getText().contains("successfully")) {
            completedSteps++;
        }
        if (irisStatusLabel.getText().contains("successfully")) {
            completedSteps++;
        }
        
        progress = (double) completedSteps / 4.0;
        overallProgress.setProgress(progress);
        
        if (completedSteps == 0) {
            statusLabel.setText("Ready to begin biometric setup");
        } else if (completedSteps < 4) {
            statusLabel.setText(String.format("Biometric setup progress: %d/4 completed", completedSteps));
        } else {
            statusLabel.setText("All biometric authentication methods configured successfully! ✓");
            statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }
    }
    
    public Scene createScene() {
        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        
        return new Scene(scrollPane, 900, 700);
    }
    
    public void show() {
        Scene scene = createScene();
        primaryStage.setScene(scene);
        primaryStage.setTitle("DevVault - Biometric Authentication Setup");
        primaryStage.show();
    }
}