package com.devvault.offline;

import com.devvault.offline.dto.SnapshotExport;
import com.devvault.offline.model.Project;
import com.devvault.offline.repository.DatasetRepository;
import com.devvault.offline.repository.GitConnectionRepository;
import com.devvault.offline.repository.ProjectRepository;
import com.devvault.offline.service.SnapshotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SnapshotServiceUnitTest {

    ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
    DatasetRepository datasetRepository = Mockito.mock(DatasetRepository.class);
    GitConnectionRepository gitConnectionRepository = Mockito.mock(GitConnectionRepository.class);

    SnapshotService snapshotService;

    @BeforeEach
    public void setup() {
        snapshotService = new SnapshotService();
        // inject mocks via reflection (fields are package-private in this module)
        snapshotService.projectRepository = projectRepository;
        snapshotService.datasetRepository = datasetRepository;
        snapshotService.gitConnectionRepository = gitConnectionRepository;
    }

    @Test
    public void importClearsIdsAndSaves() {
        Project p = new Project();
        p.setId(123L);
        p.setName("from-snap");

        SnapshotExport snap = new SnapshotExport(Arrays.asList(p), Collections.emptyList(), Collections.emptyList());

        snapshotService.importSnapshot(snap);

        ArgumentCaptor<Iterable<Project>> captor = ArgumentCaptor.forClass(Iterable.class);
        verify(projectRepository).saveAll(captor.capture());
        Iterable<Project> saved = captor.getValue();
        Project savedP = saved.iterator().next();
        assertThat(savedP.getId()).isNull();
        assertThat(savedP.getName()).isEqualTo("from-snap");
    }
}
