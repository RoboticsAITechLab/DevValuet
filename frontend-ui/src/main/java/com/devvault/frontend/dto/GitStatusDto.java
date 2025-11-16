package com.devvault.frontend.dto;

import java.util.List;

public class GitStatusDto {
    private String branch;
    private boolean hasUncommittedChanges;
    private List<String> modifiedFiles;
    private List<String> addedFiles;
    private List<String> deletedFiles;
    private List<String> untrackedFiles;
    private List<String> stagedFiles;
    private int commitsAhead;
    private int commitsBehind;
    private boolean isClean;

    // Constructors
    public GitStatusDto() {}

    public GitStatusDto(String branch, boolean hasUncommittedChanges) {
        this.branch = branch;
        this.hasUncommittedChanges = hasUncommittedChanges;
        this.isClean = !hasUncommittedChanges;
    }

    // Getters and Setters
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public boolean isHasUncommittedChanges() { return hasUncommittedChanges; }
    public void setHasUncommittedChanges(boolean hasUncommittedChanges) { 
        this.hasUncommittedChanges = hasUncommittedChanges;
        this.isClean = !hasUncommittedChanges;
    }

    public List<String> getModifiedFiles() { return modifiedFiles; }
    public void setModifiedFiles(List<String> modifiedFiles) { this.modifiedFiles = modifiedFiles; }

    public List<String> getAddedFiles() { return addedFiles; }
    public void setAddedFiles(List<String> addedFiles) { this.addedFiles = addedFiles; }

    public List<String> getDeletedFiles() { return deletedFiles; }
    public void setDeletedFiles(List<String> deletedFiles) { this.deletedFiles = deletedFiles; }

    public List<String> getUntrackedFiles() { return untrackedFiles; }
    public void setUntrackedFiles(List<String> untrackedFiles) { this.untrackedFiles = untrackedFiles; }

    public List<String> getStagedFiles() { return stagedFiles; }
    public void setStagedFiles(List<String> stagedFiles) { this.stagedFiles = stagedFiles; }

    public int getCommitsAhead() { return commitsAhead; }
    public void setCommitsAhead(int commitsAhead) { this.commitsAhead = commitsAhead; }

    public int getCommitsBehind() { return commitsBehind; }
    public void setCommitsBehind(int commitsBehind) { this.commitsBehind = commitsBehind; }

    public boolean isClean() { return isClean; }
    public void setClean(boolean isClean) { this.isClean = isClean; }

    public int getTotalChanges() {
        int total = 0;
        if (modifiedFiles != null) total += modifiedFiles.size();
        if (addedFiles != null) total += addedFiles.size();
        if (deletedFiles != null) total += deletedFiles.size();
        if (untrackedFiles != null) total += untrackedFiles.size();
        return total;
    }
    
    // Additional methods needed by services
    public String getCurrentBranch() {
        return this.branch;
    }

    @Override
    public String toString() {
        return "GitStatusDto{" +
                "branch='" + branch + '\'' +
                ", hasUncommittedChanges=" + hasUncommittedChanges +
                ", totalChanges=" + getTotalChanges() +
                ", commitsAhead=" + commitsAhead +
                ", commitsBehind=" + commitsBehind +
                ", isClean=" + isClean +
                '}';
    }
}