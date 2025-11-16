package com.devvault.frontend.services;

import com.devvault.frontend.http.HttpClientService;
import com.devvault.frontend.dto.RepositoryDto;
import com.devvault.frontend.dto.BranchDto;
import com.devvault.frontend.dto.CommitDto;
import com.devvault.frontend.dto.GitStatusDto;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Enterprise Git Service
 * Handles Git operations, repository management, and version control
 */
public class GitService {
    
    private static final Logger logger = LoggerFactory.getLogger(GitService.class);
    
    private final HttpClientService httpClient;
    private final ObservableList<RepositoryDto> repositories;
    private final ObservableList<BranchDto> branches;
    private final ObservableList<CommitDto> commits;
    
    // Git Status Properties
    private final StringProperty currentRepository = new SimpleStringProperty("");
    private final StringProperty currentBranch = new SimpleStringProperty("");
    private final BooleanProperty hasUncommittedChanges = new SimpleBooleanProperty(false);
    private final IntegerProperty totalRepositories = new SimpleIntegerProperty(0);
    
    private static GitService instance;
    
    private GitService() {
        this.httpClient = HttpClientService.getInstance();
        this.repositories = FXCollections.observableArrayList();
        this.branches = FXCollections.observableArrayList();
        this.commits = FXCollections.observableArrayList();
        logger.info("Enterprise GitService initialized");
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized GitService getInstance() {
        if (instance == null) {
            instance = new GitService();
        }
        return instance;
    }
    
    // ============================================
    // REPOSITORY MANAGEMENT
    // ============================================
    
    /**
     * Get all repositories
     */
    public ObservableList<RepositoryDto> getAllRepositories() {
        return repositories;
    }
    
    /**
     * Refresh repositories from backend
     */
    public CompletableFuture<Void> refreshRepositories() {
        return httpClient.getCockpitCoreAsync("/api/git/repositories", new TypeReference<List<RepositoryDto>>() {})
                .thenAccept(repoList -> {
                    javafx.application.Platform.runLater(() -> {
                        repositories.clear();
                        repositories.addAll(repoList);
                        totalRepositories.set(repoList.size());
                    });
                    logger.info("Refreshed {} repositories from backend", repoList.size());
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to refresh repositories", throwable);
                    return null;
                });
    }
    
    /**
     * Clone repository
     */
    public CompletableFuture<RepositoryDto> cloneRepository(String url, String localPath, String branch) {
        logger.info("Cloning repository: {} to {}", url, localPath);
        
        GitCloneRequest request = new GitCloneRequest();
        request.setUrl(url);
        request.setLocalPath(localPath);
        request.setBranch(branch);
        
        return httpClient.postCockpitCoreAsync("/api/git/clone", request, RepositoryDto.class)
                .thenApply(repository -> {
                    javafx.application.Platform.runLater(() -> {
                        repositories.add(repository);
                        totalRepositories.set(repositories.size());
                        currentRepository.set(repository.getName());
                    });
                    logger.info("Repository cloned successfully: {}", repository.getName());
                    return repository;
                })
                .exceptionally(throwable -> {
                    logger.error("Repository clone failed: {}", url, throwable);
                    throw new RuntimeException("Repository clone failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Initialize new repository
     */
    public CompletableFuture<RepositoryDto> initRepository(String path, String name, boolean bareRepo) {
        logger.info("Initializing repository: {} at {}", name, path);
        
        GitInitRequest request = new GitInitRequest();
        request.setPath(path);
        request.setName(name);
        request.setBareRepo(bareRepo);
        
        return httpClient.postCockpitCoreAsync("/api/git/init", request, RepositoryDto.class)
                .thenApply(repository -> {
                    javafx.application.Platform.runLater(() -> {
                        repositories.add(repository);
                        totalRepositories.set(repositories.size());
                        currentRepository.set(repository.getName());
                    });
                    logger.info("Repository initialized successfully: {}", repository.getName());
                    return repository;
                })
                .exceptionally(throwable -> {
                    logger.error("Repository initialization failed: {}", name, throwable);
                    throw new RuntimeException("Repository initialization failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Delete repository
     */
    public CompletableFuture<Boolean> deleteRepository(String repositoryPath, boolean deleteFiles) {
        logger.info("Deleting repository: {} (deleteFiles: {})", repositoryPath, deleteFiles);
        
        String endpoint = "/api/git/repositories/delete?path=" + repositoryPath + "&deleteFiles=" + deleteFiles;
        
        return httpClient.deleteCockpitCoreAsync(endpoint)
                .thenApply(result -> {
                    javafx.application.Platform.runLater(() -> {
                        repositories.removeIf(repo -> repo.getPath().equals(repositoryPath));
                        totalRepositories.set(repositories.size());
                    });
                    logger.info("Repository deleted successfully: {}", repositoryPath);
                    return true;
                })
                .exceptionally(throwable -> {
                    logger.error("Repository deletion failed: {}", repositoryPath, throwable);
                    return false;
                });
    }
    
    // ============================================
    // BRANCH MANAGEMENT
    // ============================================
    
    /**
     * Get branches for repository
     */
    public CompletableFuture<List<BranchDto>> getBranches(String repositoryPath) {
        return httpClient.getCockpitCoreAsync("/api/git/repositories/" + repositoryPath + "/branches", 
                new TypeReference<List<BranchDto>>() {})
                .thenApply(branchList -> {
                    javafx.application.Platform.runLater(() -> {
                        branches.clear();
                        branches.addAll(branchList);
                    });
                    logger.info("Retrieved {} branches for repository: {}", branchList.size(), repositoryPath);
                    return branchList;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get branches for repository: {}", repositoryPath, throwable);
                    return List.of();
                });
    }
    
    /**
     * Create new branch
     */
    public CompletableFuture<BranchDto> createBranch(String repositoryPath, String branchName, String fromBranch) {
        logger.info("Creating branch: {} from {} in repository: {}", branchName, fromBranch, repositoryPath);
        
        GitCreateBranchRequest request = new GitCreateBranchRequest();
        request.setRepositoryPath(repositoryPath);
        request.setBranchName(branchName);
        request.setFromBranch(fromBranch);
        
        return httpClient.postCockpitCoreAsync("/api/git/branches/create", request, BranchDto.class)
                .thenApply(branch -> {
                    javafx.application.Platform.runLater(() -> {
                        branches.add(branch);
                    });
                    logger.info("Branch created successfully: {}", branchName);
                    return branch;
                })
                .exceptionally(throwable -> {
                    logger.error("Branch creation failed: {}", branchName, throwable);
                    throw new RuntimeException("Branch creation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Switch branch
     */
    public CompletableFuture<Boolean> switchBranch(String repositoryPath, String branchName) {
        logger.info("Switching to branch: {} in repository: {}", branchName, repositoryPath);
        
        GitSwitchBranchRequest request = new GitSwitchBranchRequest();
        request.setRepositoryPath(repositoryPath);
        request.setBranchName(branchName);
        
        return httpClient.postCockpitCoreAsync("/api/git/branches/switch", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        javafx.application.Platform.runLater(() -> {
                            currentBranch.set(branchName);
                        });
                        logger.info("Switched to branch: {}", branchName);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Branch switch failed: {}", branchName, throwable);
                    return false;
                });
    }
    
    /**
     * Merge branch
     */
    public CompletableFuture<GitMergeResultDto> mergeBranch(String repositoryPath, String sourceBranch, String targetBranch) {
        logger.info("Merging branch: {} into {} in repository: {}", sourceBranch, targetBranch, repositoryPath);
        
        GitMergeRequest request = new GitMergeRequest();
        request.setRepositoryPath(repositoryPath);
        request.setSourceBranch(sourceBranch);
        request.setTargetBranch(targetBranch);
        
        return httpClient.postCockpitCoreAsync("/api/git/branches/merge", request, GitMergeResultDto.class)
                .thenApply(result -> {
                    logger.info("Branch merge completed - Conflicts: {}", result.hasConflicts());
                    return result;
                })
                .exceptionally(throwable -> {
                    logger.error("Branch merge failed: {} -> {}", sourceBranch, targetBranch, throwable);
                    throw new RuntimeException("Branch merge failed: " + throwable.getMessage(), throwable);
                });
    }
    
    // ============================================
    // COMMIT OPERATIONS
    // ============================================
    
    /**
     * Get commit history
     */
    public CompletableFuture<List<CommitDto>> getCommitHistory(String repositoryPath, String branch, int limit) {
        return httpClient.getCockpitCoreAsync("/api/git/repositories/" + repositoryPath + "/commits?branch=" + branch + "&limit=" + limit, 
                new TypeReference<List<CommitDto>>() {})
                .thenApply(commitList -> {
                    javafx.application.Platform.runLater(() -> {
                        commits.clear();
                        commits.addAll(commitList);
                    });
                    logger.info("Retrieved {} commits for repository: {}", commitList.size(), repositoryPath);
                    return commitList;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get commit history: {}", repositoryPath, throwable);
                    return List.of();
                });
    }
    
    /**
     * Create commit
     */
    public CompletableFuture<CommitDto> createCommit(String repositoryPath, String message, String author, List<String> files) {
        logger.info("Creating commit in repository: {} with message: {}", repositoryPath, message);
        
        GitCommitRequest request = new GitCommitRequest();
        request.setRepositoryPath(repositoryPath);
        request.setMessage(message);
        request.setAuthor(author);
        request.setFiles(files);
        
        return httpClient.postCockpitCoreAsync("/api/git/commit", request, CommitDto.class)
                .thenApply(commit -> {
                    javafx.application.Platform.runLater(() -> {
                        commits.add(0, commit); // Add to beginning of list
                        hasUncommittedChanges.set(false);
                    });
                    logger.info("Commit created successfully: {}", commit.getHash());
                    return commit;
                })
                .exceptionally(throwable -> {
                    logger.error("Commit creation failed: {}", message, throwable);
                    throw new RuntimeException("Commit creation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Get repository status
     */
    public CompletableFuture<GitStatusDto> getRepositoryStatus(String repositoryPath) {
        return httpClient.getCockpitCoreAsync("/api/git/repositories/" + repositoryPath + "/status", GitStatusDto.class)
                .thenApply(status -> {
                    javafx.application.Platform.runLater(() -> {
                        hasUncommittedChanges.set(!status.isClean());
                        currentBranch.set(status.getCurrentBranch());
                    });
                    return status;
                })
                .exceptionally(throwable -> {
                    logger.error("Failed to get repository status: {}", repositoryPath, throwable);
                    return new GitStatusDto();
                });
    }
    
    /**
     * Revert commit
     */
    public CompletableFuture<Boolean> revertCommit(String repositoryPath, String commitHash) {
        logger.info("Reverting commit: {} in repository: {}", commitHash, repositoryPath);
        
        GitRevertRequest request = new GitRevertRequest();
        request.setRepositoryPath(repositoryPath);
        request.setCommitHash(commitHash);
        
        return httpClient.postCockpitCoreAsync("/api/git/revert", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Commit reverted successfully: {}", commitHash);
                        // Refresh commit history
                        getCommitHistory(repositoryPath, currentBranch.get(), 50);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Commit revert failed: {}", commitHash, throwable);
                    return false;
                });
    }
    
    // ============================================
    // REMOTE OPERATIONS
    // ============================================
    
    /**
     * Push to remote
     */
    public CompletableFuture<Boolean> pushToRemote(String repositoryPath, String remoteName, String branch) {
        logger.info("Pushing branch: {} to remote: {} in repository: {}", branch, remoteName, repositoryPath);
        
        GitPushRequest request = new GitPushRequest();
        request.setRepositoryPath(repositoryPath);
        request.setRemoteName(remoteName);
        request.setBranch(branch);
        
        return httpClient.postCockpitCoreAsync("/api/git/push", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Push completed successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Push failed: {} -> {}", branch, remoteName, throwable);
                    return false;
                });
    }
    
    /**
     * Pull from remote
     */
    public CompletableFuture<Boolean> pullFromRemote(String repositoryPath, String remoteName, String branch) {
        logger.info("Pulling branch: {} from remote: {} in repository: {}", branch, remoteName, repositoryPath);
        
        GitPullRequest request = new GitPullRequest();
        request.setRepositoryPath(repositoryPath);
        request.setRemoteName(remoteName);
        request.setBranch(branch);
        
        return httpClient.postCockpitCoreAsync("/api/git/pull", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Pull completed successfully");
                        // Refresh commit history after pull
                        getCommitHistory(repositoryPath, branch, 50);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Pull failed: {} <- {}", branch, remoteName, throwable);
                    return false;
                });
    }
    
    /**
     * Add remote
     */
    public CompletableFuture<Boolean> addRemote(String repositoryPath, String remoteName, String url) {
        logger.info("Adding remote: {} -> {} in repository: {}", remoteName, url, repositoryPath);
        
        GitAddRemoteRequest request = new GitAddRemoteRequest();
        request.setRepositoryPath(repositoryPath);
        request.setRemoteName(remoteName);
        request.setUrl(url);
        
        return httpClient.postCockpitCoreAsync("/api/git/remotes/add", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Remote added successfully: {}", remoteName);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Remote addition failed: {}", remoteName, throwable);
                    return false;
                });
    }
    
    // ============================================
    // FILE OPERATIONS
    // ============================================
    
    /**
     * Stage files
     */
    public CompletableFuture<Boolean> stageFiles(String repositoryPath, List<String> files) {
        logger.info("Staging {} files in repository: {}", files.size(), repositoryPath);
        
        GitStageRequest request = new GitStageRequest();
        request.setRepositoryPath(repositoryPath);
        request.setFiles(files);
        
        return httpClient.postCockpitCoreAsync("/api/git/stage", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Files staged successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("File staging failed", throwable);
                    return false;
                });
    }
    
    /**
     * Unstage files
     */
    public CompletableFuture<Boolean> unstageFiles(String repositoryPath, List<String> files) {
        logger.info("Unstaging {} files in repository: {}", files.size(), repositoryPath);
        
        GitUnstageRequest request = new GitUnstageRequest();
        request.setRepositoryPath(repositoryPath);
        request.setFiles(files);
        
        return httpClient.postCockpitCoreAsync("/api/git/unstage", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Files unstaged successfully");
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("File unstaging failed", throwable);
                    return false;
                });
    }
    
    /**
     * Get file diff
     */
    public CompletableFuture<String> getFileDiff(String repositoryPath, String filePath, String fromCommit, String toCommit) {
        String endpoint = String.format("/api/git/repositories/%s/diff?file=%s&from=%s&to=%s", 
                repositoryPath, filePath, fromCommit, toCommit);
        
        return httpClient.getCockpitCoreAsync(endpoint, String.class)
                .exceptionally(throwable -> {
                    logger.error("Failed to get file diff: {}", filePath, throwable);
                    return "";
                });
    }
    
    // ============================================
    // STASH OPERATIONS
    // ============================================
    
    /**
     * Stash changes
     */
    public CompletableFuture<GitStashDto> stashChanges(String repositoryPath, String message) {
        logger.info("Stashing changes in repository: {} with message: {}", repositoryPath, message);
        
        GitStashRequest request = new GitStashRequest();
        request.setRepositoryPath(repositoryPath);
        request.setMessage(message);
        
        return httpClient.postCockpitCoreAsync("/api/git/stash", request, GitStashDto.class)
                .thenApply(stash -> {
                    logger.info("Changes stashed successfully: {}", stash.getId());
                    return stash;
                })
                .exceptionally(throwable -> {
                    logger.error("Stash operation failed", throwable);
                    throw new RuntimeException("Stash operation failed: " + throwable.getMessage(), throwable);
                });
    }
    
    /**
     * Apply stash
     */
    public CompletableFuture<Boolean> applyStash(String repositoryPath, String stashId) {
        logger.info("Applying stash: {} in repository: {}", stashId, repositoryPath);
        
        GitApplyStashRequest request = new GitApplyStashRequest();
        request.setRepositoryPath(repositoryPath);
        request.setStashId(stashId);
        
        return httpClient.postCockpitCoreAsync("/api/git/stash/apply", request, Boolean.class)
                .thenApply(success -> {
                    if (success) {
                        logger.info("Stash applied successfully: {}", stashId);
                    }
                    return success;
                })
                .exceptionally(throwable -> {
                    logger.error("Stash apply failed: {}", stashId, throwable);
                    return false;
                });
    }
    
    // ============================================
    // PROPERTY ACCESSORS
    // ============================================
    
    public StringProperty currentRepositoryProperty() { return currentRepository; }
    public String getCurrentRepository() { return currentRepository.get(); }
    
    public StringProperty currentBranchProperty() { return currentBranch; }
    public String getCurrentBranch() { return currentBranch.get(); }
    
    public BooleanProperty hasUncommittedChangesProperty() { return hasUncommittedChanges; }
    public boolean hasUncommittedChanges() { return hasUncommittedChanges.get(); }
    
    public IntegerProperty totalRepositoriesProperty() { return totalRepositories; }
    public int getTotalRepositories() { return totalRepositories.get(); }
    
    public ObservableList<BranchDto> getBranches() { return branches; }
    public ObservableList<CommitDto> getCommits() { return commits; }
    
    // ============================================
    // DTO CLASSES (Request Objects)
    // ============================================
    
    private static class GitCloneRequest {
        private String url;
        private String localPath;
        private String branch;
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        
        public String getLocalPath() { return localPath; }
        public void setLocalPath(String localPath) { this.localPath = localPath; }
        
        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
    }
    
    private static class GitInitRequest {
        private String path;
        private String name;
        private boolean bareRepo;
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public boolean isBareRepo() { return bareRepo; }
        public void setBareRepo(boolean bareRepo) { this.bareRepo = bareRepo; }
    }
    
    private static class GitCreateBranchRequest {
        private String repositoryPath;
        private String branchName;
        private String fromBranch;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getBranchName() { return branchName; }
        public void setBranchName(String branchName) { this.branchName = branchName; }
        
        public String getFromBranch() { return fromBranch; }
        public void setFromBranch(String fromBranch) { this.fromBranch = fromBranch; }
    }
    
    private static class GitSwitchBranchRequest {
        private String repositoryPath;
        private String branchName;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getBranchName() { return branchName; }
        public void setBranchName(String branchName) { this.branchName = branchName; }
    }
    
    private static class GitMergeRequest {
        private String repositoryPath;
        private String sourceBranch;
        private String targetBranch;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getSourceBranch() { return sourceBranch; }
        public void setSourceBranch(String sourceBranch) { this.sourceBranch = sourceBranch; }
        
        public String getTargetBranch() { return targetBranch; }
        public void setTargetBranch(String targetBranch) { this.targetBranch = targetBranch; }
    }
    
    private static class GitCommitRequest {
        private String repositoryPath;
        private String message;
        private String author;
        private List<String> files;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public List<String> getFiles() { return files; }
        public void setFiles(List<String> files) { this.files = files; }
    }
    
    private static class GitRevertRequest {
        private String repositoryPath;
        private String commitHash;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getCommitHash() { return commitHash; }
        public void setCommitHash(String commitHash) { this.commitHash = commitHash; }
    }
    
    private static class GitPushRequest {
        private String repositoryPath;
        private String remoteName;
        private String branch;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getRemoteName() { return remoteName; }
        public void setRemoteName(String remoteName) { this.remoteName = remoteName; }
        
        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
    }
    
    private static class GitPullRequest {
        private String repositoryPath;
        private String remoteName;
        private String branch;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getRemoteName() { return remoteName; }
        public void setRemoteName(String remoteName) { this.remoteName = remoteName; }
        
        public String getBranch() { return branch; }
        public void setBranch(String branch) { this.branch = branch; }
    }
    
    private static class GitAddRemoteRequest {
        private String repositoryPath;
        private String remoteName;
        private String url;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getRemoteName() { return remoteName; }
        public void setRemoteName(String remoteName) { this.remoteName = remoteName; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
    
    private static class GitStageRequest {
        private String repositoryPath;
        private List<String> files;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public List<String> getFiles() { return files; }
        public void setFiles(List<String> files) { this.files = files; }
    }
    
    private static class GitUnstageRequest {
        private String repositoryPath;
        private List<String> files;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public List<String> getFiles() { return files; }
        public void setFiles(List<String> files) { this.files = files; }
    }
    
    private static class GitStashRequest {
        private String repositoryPath;
        private String message;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    private static class GitApplyStashRequest {
        private String repositoryPath;
        private String stashId;
        
        public String getRepositoryPath() { return repositoryPath; }
        public void setRepositoryPath(String repositoryPath) { this.repositoryPath = repositoryPath; }
        
        public String getStashId() { return stashId; }
        public void setStashId(String stashId) { this.stashId = stashId; }
    }
    
    // ============================================
    // DTO CLASSES (Response Objects)
    // ============================================
    
    public static class GitMergeResultDto {
        private boolean success;
        private boolean conflicts;
        private List<String> conflictFiles;
        private String mergeCommitHash;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public boolean hasConflicts() { return conflicts; }
        public void setConflicts(boolean conflicts) { this.conflicts = conflicts; }
        
        public List<String> getConflictFiles() { return conflictFiles; }
        public void setConflictFiles(List<String> conflictFiles) { this.conflictFiles = conflictFiles; }
        
        public String getMergeCommitHash() { return mergeCommitHash; }
        public void setMergeCommitHash(String mergeCommitHash) { this.mergeCommitHash = mergeCommitHash; }
    }
    
    public static class GitStashDto {
        private String id;
        private String message;
        private String author;
        private String timestamp;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}