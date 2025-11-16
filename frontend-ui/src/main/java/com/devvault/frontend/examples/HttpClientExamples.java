package com.devvault.frontend.examples;

import com.devvault.frontend.dto.ProjectDto;
import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.models.Project;
import com.devvault.frontend.services.ProjectService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP Client Service Usage Examples
 * 
 * This class demonstrates how to use the enterprise HTTP Client Service
 * in various scenarios throughout the frontend application.
 * 
 * Copy these patterns into your controllers for backend integration.
 */
public class HttpClientExamples {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientExamples.class);
    
    private final HttpClientService httpClient;
    private final ProjectService projectService;
    
    public HttpClientExamples() {
        this.httpClient = HttpClientService.getInstance();
        this.projectService = ProjectService.getInstance();
    }
    
    // ============================================
    // EXAMPLE 1: Basic GET Request
    // ============================================
    
    /**
     * Example: Get all projects from cockpit-core
     * Use this pattern in controllers when you need to fetch data
     */
    public void exampleGetAllProjects() {
        logger.info("Example: Getting all projects from backend");
        
        try {
            // Synchronous call (blocks UI thread - use sparingly)
            List<ProjectDto> projects = httpClient.getCockpitCore(
                    "/api/projects", 
                    new TypeReference<List<ProjectDto>>() {}
            );
            
            logger.info("Retrieved {} projects synchronously", projects.size());
            
        } catch (HttpClientService.HttpServiceException e) {
            logger.error("Failed to get projects", e);
            // Handle error - show user message, use fallback data, etc.
        }
    }
    
    // ============================================
    // EXAMPLE 2: Asynchronous GET Request (Recommended)
    // ============================================
    
    /**
     * Example: Get projects asynchronously (recommended for UI)
     * This doesn't block the JavaFX UI thread
     */
    public void exampleGetProjectsAsync() {
        logger.info("Example: Getting projects asynchronously");
        
        CompletableFuture<List<ProjectDto>> future = httpClient.getCockpitCoreAsync(
                "/api/projects", 
                new TypeReference<List<ProjectDto>>() {}
        );
        
        future.thenAccept(projects -> {
            // This runs on background thread
            logger.info("Retrieved {} projects asynchronously", projects.size());
            
            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                // Update your UI components here
                // e.g., projectTableView.getItems().setAll(projects);
                logger.info("UI updated with {} projects", projects.size());
            });
            
        }).exceptionally(throwable -> {
            // Handle errors
            logger.error("Async project fetch failed", throwable);
            
            javafx.application.Platform.runLater(() -> {
                // Show error message to user
                // e.g., showErrorDialog("Failed to load projects", throwable.getMessage());
            });
            
            return null;
        });
    }
    
    // ============================================
    // EXAMPLE 3: POST Request (Create new resource)
    // ============================================
    
    /**
     * Example: Create a new project
     * Use this pattern when creating new resources
     */
    public void exampleCreateProject(String projectName, String description) {
        logger.info("Example: Creating new project: {}", projectName);
        
        // Create request DTO
        ProjectDto newProject = new ProjectDto();
        newProject.setName(projectName);
        newProject.setDescription(description);
        newProject.setStatus("Active");
        newProject.setProgress(0);
        
        // Async POST request
        CompletableFuture<ProjectDto> future = httpClient.postCockpitCoreAsync(
                "/api/projects", 
                newProject, 
                ProjectDto.class
        );
        
        future.thenAccept(createdProject -> {
            logger.info("Project created successfully: {}", createdProject.getName());
            
            // Update UI
            javafx.application.Platform.runLater(() -> {
                // Add to UI list, show success message, etc.
                logger.info("UI updated with new project: {}", createdProject.getId());
            });
            
        }).exceptionally(throwable -> {
            logger.error("Project creation failed", throwable);
            
            javafx.application.Platform.runLater(() -> {
                // Show error dialog
                logger.error("Showing error to user: {}", throwable.getMessage());
            });
            
            return null;
        });
    }
    
    // ============================================
    // EXAMPLE 4: Health Check Pattern
    // ============================================
    
    /**
     * Example: Check backend service health
     * Use this pattern during app startup or to monitor connectivity
     */
    public void exampleHealthCheck() {
        logger.info("Example: Performing backend health check");
        
        CompletableFuture<HttpClientService.HealthCheckResult> future = 
                httpClient.performHealthCheck();
        
        future.thenAccept(result -> {
            logger.info("Health check completed:");
            logger.info("  Cockpit-Core: {}", result.isCockpitCoreOnline() ? "ONLINE" : "OFFLINE");
            logger.info("  Backend-Core: {}", result.isBackendCoreOnline() ? "ONLINE" : "OFFLINE"); 
            logger.info("  AI-Subsystem: {}", result.isAiSubsystemOnline() ? "ONLINE" : "OFFLINE");
            logger.info("  Overall Status: {} of 3 services online", result.getOnlineCount());
            
            javafx.application.Platform.runLater(() -> {
                // Update status indicators in UI
                // updateStatusIndicators(result);
            });
            
        }).exceptionally(throwable -> {
            logger.error("Health check failed", throwable);
            
            javafx.application.Platform.runLater(() -> {
                // Show offline mode indicator
            });
            
            return null;
        });
    }
    
    // ============================================
    // EXAMPLE 5: Error Handling Patterns
    // ============================================
    
    /**
     * Example: Comprehensive error handling
     * Use this pattern to handle different types of errors gracefully
     */
    public void exampleErrorHandling() {
        logger.info("Example: Comprehensive error handling");
        
        CompletableFuture<List<ProjectDto>> future = httpClient.getCockpitCoreAsync(
                "/api/projects", 
                new TypeReference<List<ProjectDto>>() {}
        );
        
        future.handle((result, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof java.net.ConnectException) {
                    logger.warn("Backend service unavailable - using offline mode");
                    
                    javafx.application.Platform.runLater(() -> {
                        // Load cached/sample data
                        projectService.loadSampleProjects();
                        // Show offline mode indicator
                    });
                    
                } else if (throwable instanceof java.net.SocketTimeoutException) {
                    logger.warn("Request timeout - backend may be slow");
                    
                    javafx.application.Platform.runLater(() -> {
                        // Show timeout message, offer retry
                    });
                    
                } else {
                    logger.error("Unexpected error", throwable);
                    
                    javafx.application.Platform.runLater(() -> {
                        // Show generic error message
                    });
                }
                
                return null;
            } else {
                // Success
                logger.info("Successfully retrieved {} projects", result.size());
                
                javafx.application.Platform.runLater(() -> {
                    // Update UI with data
                });
                
                return result;
            }
        });
    }
    
    // ============================================
    // EXAMPLE 6: Search with Debouncing
    // ============================================
    
    /**
     * Example: Search with debouncing to avoid too many API calls
     * Use this pattern for real-time search features
     */
    private CompletableFuture<Void> currentSearchFuture;
    
    public void exampleSearchWithDebouncing(String searchQuery) {
        logger.debug("Search query: {}", searchQuery);
        
        // Cancel previous search if still running
        if (currentSearchFuture != null && !currentSearchFuture.isDone()) {
            currentSearchFuture.cancel(true);
        }
        
        // Start new search with delay
        currentSearchFuture = CompletableFuture
                .runAsync(() -> {
                    try {
                        Thread.sleep(500); // 500ms debounce
                        if (searchQuery == null || searchQuery.trim().isEmpty()) {
                            return;
                        }
                        
                        logger.info("Executing search for: {}", searchQuery);
                    
                        projectService.searchProjects(searchQuery)
                                .thenAccept(results -> {
                                    javafx.application.Platform.runLater(() -> {
                                        // Update search results in UI
                                        logger.info("Search completed: {} results", results.size());
                                    });
                                })
                                .exceptionally(throwable -> {
                                    logger.error("Search failed", throwable);
                                    return null;
                                });
                    } catch (Exception e) {
                        logger.error("Search execution failed", e);
                    }
                });
    }
    
    // ============================================
    // CONTROLLER INTEGRATION EXAMPLE
    // ============================================
    
    /**
     * Example: How to integrate HTTP Client in a JavaFX Controller
     * Copy this pattern into your controller classes
     */
    public static class ExampleController {
        
        private final HttpClientService httpClient = HttpClientService.getInstance();
        private final ProjectService projectService = ProjectService.getInstance();
        private final Logger logger = LoggerFactory.getLogger(ExampleController.class);
        
        // FXML controls would be injected here
        // @FXML private TableView<Project> projectTable;
        // @FXML private ProgressIndicator loadingIndicator;
        // @FXML private Label statusLabel;
        
        public void initialize() {
            logger.info("Initializing controller with HTTP client integration");
            
            // Start loading indicator
            // loadingIndicator.setVisible(true);
            
            // Load initial data
            projectService.refreshProjects()
                    .thenRun(() -> {
                        javafx.application.Platform.runLater(() -> {
                            // loadingIndicator.setVisible(false);
                            // projectTable.setItems(projectService.getAllProjects());
                            logger.info("Controller initialized successfully");
                        });
                    })
                    .exceptionally(throwable -> {
                        logger.error("Controller initialization failed", throwable);
                        
                        javafx.application.Platform.runLater(() -> {
                            // loadingIndicator.setVisible(false);
                            // statusLabel.setText("Failed to load data - using offline mode");
                            projectService.loadSampleProjects();
                            // projectTable.setItems(projectService.getAllProjects());
                        });
                        
                        return null;
                    });
        }
        
        // Example button handler
        public void onRefreshButtonClicked() {
            logger.info("User requested data refresh");
            
            // Show loading state
            // loadingIndicator.setVisible(true);
            // refreshButton.setDisable(true);
            
            projectService.refreshProjects()
                    .thenRun(() -> {
                        javafx.application.Platform.runLater(() -> {
                            // loadingIndicator.setVisible(false);
                            // refreshButton.setDisable(false);
                            logger.info("Data refresh completed");
                        });
                    })
                    .exceptionally(throwable -> {
                        javafx.application.Platform.runLater(() -> {
                            // loadingIndicator.setVisible(false);
                            // refreshButton.setDisable(false);
                            // showErrorDialog("Refresh failed", throwable.getMessage());
                        });
                        return null;
                    });
        }
    }
}