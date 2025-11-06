package com.devvault.offline;

import com.devvault.offline.cli.SnapshotCli;
import com.devvault.offline.model.Project;
import com.devvault.offline.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SnapshotCliE2ETest {

    @Autowired
    SnapshotCli snapshotCli;

    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void cliExportImportRoundtrip(@TempDir Path tempDir) throws Exception {
        projectRepository.deleteAll();
        Project p = new Project();
        p.setName("cli-e2e-project");
        projectRepository.save(p);

        Path path = tempDir.resolve("snapshot.json");
        snapshotCli.exportTo(path);

        projectRepository.deleteAll();
        assertThat(projectRepository.findAll()).isEmpty();

        snapshotCli.importFrom(path);
        assertThat(projectRepository.findAll()).isNotEmpty();
    }
}
