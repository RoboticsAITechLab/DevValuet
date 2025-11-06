package com.devvault.offline.service;

import com.devvault.offline.dto.SnapshotExport;
import com.devvault.offline.model.Dataset;
import com.devvault.offline.model.GitConnection;
import com.devvault.offline.model.Project;
import com.devvault.offline.repository.DatasetRepository;
import com.devvault.offline.repository.GitConnectionRepository;
import com.devvault.offline.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SnapshotService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    GitConnectionRepository gitConnectionRepository;

    public SnapshotExport exportSnapshot() {
        List<Project> projects = projectRepository.findAll();
        List<Dataset> datasets = datasetRepository.findAll();
        List<GitConnection> gitConnections = gitConnectionRepository.findAll();
        return new SnapshotExport(projects, datasets, gitConnections);
    }

    public void importSnapshot(SnapshotExport snapshot) {
        if (snapshot.projects != null) {
            // clear IDs so JPA will insert new rows instead of attempting merges
            snapshot.projects.forEach(p -> p.setId(null));
            projectRepository.saveAll(snapshot.projects);
        }
        if (snapshot.datasets != null) {
            snapshot.datasets.forEach(d -> d.setId(null));
            datasetRepository.saveAll(snapshot.datasets);
        }
        if (snapshot.gitConnections != null) {
            snapshot.gitConnections.forEach(g -> g.setId(null));
            gitConnectionRepository.saveAll(snapshot.gitConnections);
        }
    }
}
