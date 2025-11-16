package com.devvault.frontend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommitDto {
    private String hash;
    private String shortHash;
    private String message;
    private String author;
    private String authorEmail;
    private LocalDateTime timestamp;
    private List<String> parentHashes;
    private List<String> changedFiles;
    private int additions;
    private int deletions;
    private String branch;

    // Constructors
    public CommitDto() {}

    public CommitDto(String hash, String message, String author, LocalDateTime timestamp) {
        this.hash = hash;
        this.message = message;
        this.author = author;
        this.timestamp = timestamp;
        this.shortHash = hash != null && hash.length() > 7 ? hash.substring(0, 7) : hash;
    }

    // Getters and Setters
    public String getHash() { return hash; }
    public void setHash(String hash) { 
        this.hash = hash; 
        this.shortHash = hash != null && hash.length() > 7 ? hash.substring(0, 7) : hash;
    }

    public String getShortHash() { return shortHash; }
    public void setShortHash(String shortHash) { this.shortHash = shortHash; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getAuthorEmail() { return authorEmail; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public List<String> getParentHashes() { return parentHashes; }
    public void setParentHashes(List<String> parentHashes) { this.parentHashes = parentHashes; }

    public List<String> getChangedFiles() { return changedFiles; }
    public void setChangedFiles(List<String> changedFiles) { this.changedFiles = changedFiles; }

    public int getAdditions() { return additions; }
    public void setAdditions(int additions) { this.additions = additions; }

    public int getDeletions() { return deletions; }
    public void setDeletions(int deletions) { this.deletions = deletions; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    @Override
    public String toString() {
        return "CommitDto{" +
                "hash='" + hash + '\'' +
                ", shortHash='" + shortHash + '\'' +
                ", message='" + message + '\'' +
                ", author='" + author + '\'' +
                ", timestamp=" + timestamp +
                ", additions=" + additions +
                ", deletions=" + deletions +
                '}';
    }
}