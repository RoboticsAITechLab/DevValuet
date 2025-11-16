package com.devvault.offline.dto;

import com.devvault.offline.model.Project;
import com.devvault.offline.model.Dataset;
import com.devvault.offline.model.GitConnection;

import java.util.List;

public class SnapshotExport {
    public List<Project> projects;
    public List<Dataset> datasets;
    public List<GitConnection> gitConnections;

    public SnapshotExport() {}

    public SnapshotExport(List<Project> projects, List<Dataset> datasets, List<GitConnection> gitConnections) {
        this.projects = projects;
        this.datasets = datasets;
        this.gitConnections = gitConnections;
    }
}
