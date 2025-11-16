package com.devvault.frontend.dto;

import java.time.LocalDateTime;

public class BranchDto {
    private String name;
    private String commitHash;
    private String author;
    private LocalDateTime lastCommit;
    private boolean isDefault;
    private boolean isProtected;
    private int commitsAhead;
    private int commitsBehind;

    // Constructors
    public BranchDto() {}

    public BranchDto(String name, String commitHash, String author, LocalDateTime lastCommit) {
        this.name = name;
        this.commitHash = commitHash;
        this.author = author;
        this.lastCommit = lastCommit;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCommitHash() { return commitHash; }
    public void setCommitHash(String commitHash) { this.commitHash = commitHash; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public LocalDateTime getLastCommit() { return lastCommit; }
    public void setLastCommit(LocalDateTime lastCommit) { this.lastCommit = lastCommit; }

    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public boolean isProtected() { return isProtected; }
    public void setProtected(boolean isProtected) { this.isProtected = isProtected; }

    public int getCommitsAhead() { return commitsAhead; }
    public void setCommitsAhead(int commitsAhead) { this.commitsAhead = commitsAhead; }

    public int getCommitsBehind() { return commitsBehind; }
    public void setCommitsBehind(int commitsBehind) { this.commitsBehind = commitsBehind; }

    @Override
    public String toString() {
        return "BranchDto{" +
                "name='" + name + '\'' +
                ", commitHash='" + commitHash + '\'' +
                ", author='" + author + '\'' +
                ", lastCommit=" + lastCommit +
                ", isDefault=" + isDefault +
                ", isProtected=" + isProtected +
                ", commitsAhead=" + commitsAhead +
                ", commitsBehind=" + commitsBehind +
                '}';
    }
}