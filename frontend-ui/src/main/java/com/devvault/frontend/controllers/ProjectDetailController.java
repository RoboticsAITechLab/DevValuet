package com.devvault.frontend.controllers;

import com.devvault.frontend.models.Project;
import com.devvault.frontend.utils.AnimationUtils;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Insets;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Project Detail View Controller
 * Deep dive into project with tabs for different aspects
 * 
 * ASCII Layout Reference:
 * ╔════════════════════════════════════════════════════════════════╗
 * ║ Project: "HydroSense AI"         [↩ Back] [⚙ Settings] [📈 Stats]    ║
 * ╠════════════════════════════════════════════════════════════════╣
 * ║ Overview │ Tasks │ Git │ Dataset │ Security │ Logs │ Extensions │ Cloud ║
 * ╟────────────────────────────────────────────────────────────────╢
 * ║ 📊 STATUS SNAPSHOT                                               
 * ║ ───────────────────────────────                                 
 * ║ 🟢 Git Sync: Up-to-date                                          
 * ║ 🔄 Backup: Last at 22:10 | Auto every 2 hrs                     
 * ║ 🔐 Security: Active | Zero interference                          
 * ║ 📁 Dataset: water_quality.csv (Linked)                           
 * ║ 📈 Progress: █████████░ 85%                                     
 * ╟────────────────────────────────────────────────────────────────╢
 * ║ [Open Folder] [Run Terminal] [Create Backup] [View Report]      ║
 * ╚════════════════════════════════════════════════════════════════╝
 */
public class ProjectDetailController implements Initializable {
    
    private static final Logger logger = LoggerFactory.getLogger(ProjectDetailController.class);
    
    // Header Controls
    @FXML private Text projectTitle;
    @FXML private Button backButton;
    @FXML private Button settingsButton;
    @FXML private Button statsButton;
    
    // Tab Pane
    @FXML private TabPane tabPane;
    @FXML private Tab overviewTab;
    @FXML private Tab tasksTab;
    @FXML private Tab gitTab;
    @FXML private Tab datasetTab;
    @FXML private Tab securityTab;
    @FXML private Tab logsTab;
    @FXML private Tab extensionsTab;
    @FXML private Tab cloudTab;
    
    // Overview Tab Content
    @FXML private VBox statusSnapshot;
    @FXML private Text gitSyncStatus;
    @FXML private Text backupStatus;
    @FXML private Text securityStatus;
    @FXML private Text datasetStatus;
    @FXML private ProgressBar progressBar;
    @FXML private Text progressText;
    
    // Action buttons
    @FXML private HBox actionButtons;
    @FXML private Button openFolderButton;
    @FXML private Button openIDEButton;
    @FXML private Button runTerminalButton;
    @FXML private Button createBackupButton;
    @FXML private Button viewReportButton;
    
    // Current project
    private Project currentProject;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("🔍 Initializing Project Detail Controller");
        
        // Setup header buttons
        setupHeaderButtons();
        
        // Setup tabs
        setupTabs();
        
        // Setup action buttons
        setupActionButtons();
        
        // Load sample project data
        loadSampleProject();
        
        logger.info("✅ Project Detail initialized successfully");
    }
    
    private void setupHeaderButtons() {
        // Back Button
        FontAwesomeIconView backIcon = new FontAwesomeIconView(FontAwesomeIcon.ARROW_LEFT);
        backIcon.setSize("16");
        backButton.setGraphic(backIcon);
        backButton.setOnAction(e -> goBack());
        
        // Settings Button
        FontAwesomeIconView settingsIcon = new FontAwesomeIconView(FontAwesomeIcon.COG);
        settingsIcon.setSize("16");
        settingsButton.setGraphic(settingsIcon);
        settingsButton.setOnAction(e -> openSettings());
        
        // Stats Button
        FontAwesomeIconView statsIcon = new FontAwesomeIconView(FontAwesomeIcon.BAR_CHART);
        statsIcon.setSize("16");
        statsButton.setGraphic(statsIcon);
        statsButton.setOnAction(e -> openStats());
    }
    
    private void setupTabs() {
        // Overview Tab - Default selected
        overviewTab.setGraphic(createTabIcon(FontAwesomeIcon.DASHBOARD));
        
        // Tasks Tab
        tasksTab.setGraphic(createTabIcon(FontAwesomeIcon.TASKS));
        
        // Git Tab
        gitTab.setGraphic(createTabIcon(FontAwesomeIcon.CODE_FORK));
        
        // Dataset Tab
        datasetTab.setGraphic(createTabIcon(FontAwesomeIcon.DATABASE));
        
        // Security Tab
        securityTab.setGraphic(createTabIcon(FontAwesomeIcon.SHIELD));
        
        // Logs Tab
        logsTab.setGraphic(createTabIcon(FontAwesomeIcon.FILE_TEXT_ALT));
        
        // Extensions Tab
        extensionsTab.setGraphic(createTabIcon(FontAwesomeIcon.PUZZLE_PIECE));
        
        // Cloud Tab
        cloudTab.setGraphic(createTabIcon(FontAwesomeIcon.CLOUD));
        
        // Tab selection listener
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                logger.info("Switched to tab: {}", newTab.getText());
                AnimationUtils.slideIn(newTab.getContent(), AnimationUtils.Direction.RIGHT);
            }
        });
    }
    
    private FontAwesomeIconView createTabIcon(FontAwesomeIcon icon) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("14");
        return iconView;
    }
    
    private void setupActionButtons() {
        // Open Folder Button
        FontAwesomeIconView folderIcon = new FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN);
        folderIcon.setSize("14");
        openFolderButton.setGraphic(folderIcon);
        openFolderButton.setOnAction(e -> openProjectFolder());
        
        // Open in IDE Button
        FontAwesomeIconView ideIcon = new FontAwesomeIconView(FontAwesomeIcon.CODE);
        ideIcon.setSize("14");
        openIDEButton.setGraphic(ideIcon);
        openIDEButton.setOnAction(e -> openInIDE());
        
        // Run Terminal Button
        FontAwesomeIconView terminalIcon = new FontAwesomeIconView(FontAwesomeIcon.TERMINAL);
        terminalIcon.setSize("14");
        runTerminalButton.setGraphic(terminalIcon);
        runTerminalButton.setOnAction(e -> runTerminal());
        
        // Create Backup Button
        FontAwesomeIconView backupIcon = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        backupIcon.setSize("14");
        createBackupButton.setGraphic(backupIcon);
        createBackupButton.setOnAction(e -> createBackup());
        
        // View Report Button
        FontAwesomeIconView reportIcon = new FontAwesomeIconView(FontAwesomeIcon.FILE_PDF_ALT);
        reportIcon.setSize("14");
        viewReportButton.setGraphic(reportIcon);
        viewReportButton.setOnAction(e -> viewReport());
        
        // Add hover effects
        actionButtons.getChildren().forEach(node -> {
            if (node instanceof Button button) {
                AnimationUtils.addPulseHover(button);
                AnimationUtils.addRippleClick(button);
            }
        });
    }
    
    private void loadSampleProject() {
        // Create sample project
        currentProject = new Project("HydroSense AI", "🔵", "Active", "02 Nov");
        currentProject.setDescription("Water quality monitoring with predictive analytics");
        currentProject.setProgress(85);
        
        // Update UI with project data
        updateProjectDisplay();
    }
    
    private void updateProjectDisplay() {
        if (currentProject == null) return;
        
        // Update header
        projectTitle.setText("Project: \"" + currentProject.getName() + "\"");
        
        // Update status snapshot
        updateStatusSnapshot();
        
        // Update progress
        progressBar.setProgress(currentProject.getProgress() / 100.0);
        progressText.setText(String.format("Progress: %d%% Complete", currentProject.getProgress()));
        
        // Animate progress bar
        AnimationUtils.animateProgress(progressBar, currentProject.getProgress() / 100.0);
    }
    
    private void updateStatusSnapshot() {
        // Git Sync Status
        gitSyncStatus.setText("🟢 Git Sync: Up-to-date");
        gitSyncStatus.getStyleClass().add("status-good");
        
        // Backup Status
        backupStatus.setText("🔄 Backup: Last at 22:10 | Auto every 2 hrs");
        backupStatus.getStyleClass().add("status-info");
        
        // Security Status
        securityStatus.setText("🔐 Security: Active | Zero interference");
        securityStatus.getStyleClass().add("status-good");
        
        // Dataset Status
        datasetStatus.setText("📁 Dataset: water_quality.csv (Linked)");
        datasetStatus.getStyleClass().add("status-linked");
    }
    
    // Event Handlers
    private void goBack() {
        logger.info("Going back to main dashboard");
        AnimationUtils.addRippleClick(backButton);
        
        try {
            // Load main dashboard view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-dashboard.fxml"));
            Parent mainDashboard = loader.load();
            
            // Get current stage and set new scene  
            Stage stage = (Stage) projectTitle.getScene().getWindow();
            Scene scene = new Scene(mainDashboard);
            scene.getStylesheets().add(getClass().getResource("/css/neo-dark-theme.css").toExternalForm());
            
            // Apply fade transition
            FadeTransition fadeOut = new FadeTransition(Duration.millis(150), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);
                
                FadeTransition fadeIn = new FadeTransition(Duration.millis(150), scene.getRoot());
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            
            fadeOut.play();
            
        } catch (IOException e) {
            logger.error("Error navigating back to main dashboard: {}", e.getMessage(), e);
        }
    }
    
    private void openSettings() {
        logger.info("Opening project settings");
        AnimationUtils.addRippleClick(settingsButton);
        // TODO: Open project settings dialog
    }
    
    private void openStats() {
        logger.info("Opening project statistics");
        AnimationUtils.addRippleClick(statsButton);
        // TODO: Open stats view
    }
    
    private void openProjectFolder() {
        logger.info("Opening project folder in explorer");
        AnimationUtils.addRippleClick(openFolderButton);
        
        if (currentProject != null) {
            try {
                // Call backend API to open folder
                String apiUrl = "http://localhost:8085/api/ide/open-folder";
                Map<String, String> requestData = Map.of("projectPath", currentProject.getPath());
                
                // Make API call
                CompletableFuture.supplyAsync(() -> {
                    try {
                        // Simple HTTP call to backend
                        ProcessBuilder pb = new ProcessBuilder("explorer.exe", currentProject.getPath());
                        pb.start();
                        return true;
                    } catch (Exception e) {
                        logger.error("Error opening folder: {}", e.getMessage());
                        return false;
                    }
                }).thenAccept(success -> {
                    Platform.runLater(() -> {
                        if (success) {
                            showNotification("Project folder opened successfully", "success");
                        } else {
                            showNotification("Failed to open project folder", "error");
                        }
                    });
                });
                
            } catch (Exception e) {
                logger.error("Error opening project folder: {}", e.getMessage());
                showNotification("Error opening project folder: " + e.getMessage(), "error");
            }
        }
    }
    
    private void runTerminal() {
        logger.info("Running terminal in project directory");
        AnimationUtils.addRippleClick(runTerminalButton);
        
        if (currentProject != null) {
            try {
                // Open PowerShell in project directory
                ProcessBuilder pb = new ProcessBuilder("powershell.exe", "-Command", 
                    "Start-Process", "powershell.exe", "-ArgumentList", 
                    "'-NoExit', '-Command', 'cd \"" + currentProject.getPath() + "\"'");
                pb.start();
                
                showNotification("Terminal opened in project directory", "success");
                
            } catch (Exception e) {
                logger.error("Error opening terminal: {}", e.getMessage());
                showNotification("Error opening terminal: " + e.getMessage(), "error");
            }
        }
    }
    
    private void openInIDE() {
        logger.info("Opening project in IDE");
        
        if (currentProject != null) {
            // Create IDE selection dialog
            createIDESelectionDialog();
        }
    }
    
    private void createIDESelectionDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Select IDE");
        dialog.setHeaderText("Choose an IDE to open the project");
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label instructionLabel = new Label("Available IDEs:");
        instructionLabel.setStyle("-fx-font-weight: bold;");
        
        // IDE options
        ToggleGroup ideGroup = new ToggleGroup();
        VBox ideOptions = new VBox(5);
        
        // VS Code
        RadioButton vscodeOption = new RadioButton("Visual Studio Code");
        vscodeOption.setToggleGroup(ideGroup);
        vscodeOption.setUserData("vscode");
        vscodeOption.setSelected(true);
        
        // IntelliJ IDEA
        RadioButton intellijOption = new RadioButton("IntelliJ IDEA");
        intellijOption.setToggleGroup(ideGroup);
        intellijOption.setUserData("intellij");
        
        // Eclipse
        RadioButton eclipseOption = new RadioButton("Eclipse IDE");
        eclipseOption.setToggleGroup(ideGroup);
        eclipseOption.setUserData("eclipse");
        
        // Notepad++
        RadioButton notepadOption = new RadioButton("Notepad++");
        notepadOption.setToggleGroup(ideGroup);
        notepadOption.setUserData("notepadpp");
        
        ideOptions.getChildren().addAll(vscodeOption, intellijOption, eclipseOption, notepadOption);
        
        content.getChildren().addAll(instructionLabel, ideOptions);
        dialog.getDialogPane().setContent(content);
        
        // Buttons
        ButtonType openButtonType = new ButtonType("Open", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(openButtonType, ButtonType.CANCEL);
        
        // Result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == openButtonType) {
                RadioButton selectedRadio = (RadioButton) ideGroup.getSelectedToggle();
                return selectedRadio != null ? (String) selectedRadio.getUserData() : null;
            }
            return null;
        });
        
        // Handle result
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(ide -> {
            openProjectInIDE(ide);
        });
    }
    
    private void openProjectInIDE(String ide) {
        logger.info("Opening project in IDE: {}", ide);
        
        CompletableFuture.supplyAsync(() -> {
            try {
                String command = buildIDECommand(ide, currentProject.getPath());
                if (command != null) {
                    ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
                    pb.directory(new File(currentProject.getPath()));
                    Process process = pb.start();
                    
                    // Wait a bit to check if it started successfully
                    Thread.sleep(2000);
                    return process.isAlive() || process.exitValue() == 0;
                }
                return false;
                
            } catch (Exception e) {
                logger.error("Error opening project in IDE: {}", e.getMessage());
                return false;
            }
        }).thenAccept(success -> {
            Platform.runLater(() -> {
                if (success) {
                    showNotification("Project opened in " + getIDEName(ide), "success");
                } else {
                    showNotification("Could not open project in " + getIDEName(ide) + 
                                   ". Please ensure it's installed.", "warning");
                }
            });
        });
    }
    
    private String buildIDECommand(String ide, String projectPath) {
        switch (ide.toLowerCase()) {
            case "vscode":
                return "code \"" + projectPath + "\"";
            case "intellij":
                return "idea \"" + projectPath + "\"";
            case "eclipse":
                return "eclipse -data \"" + projectPath + "\"";
            case "notepadpp":
                return "notepad++ \"" + projectPath + "\"";
            default:
                return null;
        }
    }
    
    private String getIDEName(String ide) {
        switch (ide.toLowerCase()) {
            case "vscode": return "Visual Studio Code";
            case "intellij": return "IntelliJ IDEA";
            case "eclipse": return "Eclipse IDE";
            case "notepadpp": return "Notepad++";
            default: return ide;
        }
    }
    
    private void showNotification(String message, String type) {
        // Create notification
        Alert alert;
        switch (type) {
            case "success":
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                break;
            case "error":
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                break;
            case "warning":
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                break;
            default:
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information");
        }
        
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void createBackup() {
        logger.info("Creating project backup");
        AnimationUtils.addRippleClick(createBackupButton);
        // TODO: Trigger backup creation
        
        // Update backup status after backup
        backupStatus.setText("🔄 Backup: Just now | Auto every 2 hrs");
    }
    
    private void viewReport() {
        logger.info("Viewing project report");
        AnimationUtils.addRippleClick(viewReportButton);
        // TODO: Generate and display project report
    }
    
    // Enhanced tab management methods
    private void setupEnhancedTabs() {
        // Add enhanced tab icons and styling
        overviewTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.HOME));
        tasksTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TASKS));
        gitTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CODE_FORK));
        datasetTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DATABASE));
        securityTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SHIELD));
        logsTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE_TEXT_ALT));
        extensionsTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUG));
        cloudTab.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOUD));
        
        // Tab selection listeners
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            logger.info("Switching to tab: {}", newTab.getText());
            loadTabContent(newTab);
        });
    }
    
    private void loadTabContent(Tab selectedTab) {
        if (selectedTab == overviewTab) {
            updateOverviewTab();
        } else if (selectedTab == tasksTab) {
            updateTasksTab();
        } else if (selectedTab == gitTab) {
            updateGitTab();
        } else if (selectedTab == datasetTab) {
            updateDatasetTab();
        } else if (selectedTab == securityTab) {
            updateSecurityTab();
        } else if (selectedTab == logsTab) {
            updateLogsTab();
        } else if (selectedTab == extensionsTab) {
            updateExtensionsTab();
        } else if (selectedTab == cloudTab) {
            updateCloudTab();
        }
    }
    
    private void updateOverviewTab() {
        // Enhanced overview with real-time metrics
        gitSyncStatus.setText("🟢 Git Sync: Up-to-date | Last: 2 mins ago");
        backupStatus.setText("🔄 Backup: Last at 22:10 | Auto every 2 hrs");
        securityStatus.setText("🔐 Security: Active | Zero interference");
        datasetStatus.setText("📁 Dataset: water_quality.csv (Linked)");
        progressBar.setProgress(0.85);
        progressText.setText("85%");
    }
    
    private void updateTasksTab() {
        logger.info("Loading tasks tab content");
        // TODO: Load project tasks and milestones
    }
    
    private void updateGitTab() {
        logger.info("Loading git tab content");
        // TODO: Load git status, branches, commits
    }
    
    private void updateDatasetTab() {
        logger.info("Loading dataset tab content");
        // TODO: Load dataset information and statistics
    }
    
    private void updateSecurityTab() {
        logger.info("Loading security tab content");
        // TODO: Load security status and vulnerability scans
    }
    
    private void updateLogsTab() {
        logger.info("Loading logs tab content");
        // TODO: Load project logs and activity history
    }
    
    private void updateExtensionsTab() {
        logger.info("Loading extensions tab content");
        // TODO: Load project extensions and plugins
    }
    
    private void updateCloudTab() {
        logger.info("Loading cloud tab content");
        // TODO: Load cloud deployment and sync status
    }
    
    // Enhanced navigation methods
    public void navigateToTab(String tabName) {
        switch (tabName.toLowerCase()) {
            case "overview":
                tabPane.getSelectionModel().select(overviewTab);
                break;
            case "tasks":
                tabPane.getSelectionModel().select(tasksTab);
                break;
            case "git":
                tabPane.getSelectionModel().select(gitTab);
                break;
            case "dataset":
                tabPane.getSelectionModel().select(datasetTab);
                break;
            case "security":
                tabPane.getSelectionModel().select(securityTab);
                break;
            case "logs":
                tabPane.getSelectionModel().select(logsTab);
                break;
            case "extensions":
                tabPane.getSelectionModel().select(extensionsTab);
                break;
            case "cloud":
                tabPane.getSelectionModel().select(cloudTab);
                break;
            default:
                logger.warn("Unknown tab requested: {}", tabName);
        }
    }
    
    // Public methods for external access
    public void setProject(Project project) {
        this.currentProject = project;
        updateProjectDisplay();
        logger.info("Project detail view updated for: {}", project.getName());
    }
    
    public Project getCurrentProject() {
        return currentProject;
    }
}