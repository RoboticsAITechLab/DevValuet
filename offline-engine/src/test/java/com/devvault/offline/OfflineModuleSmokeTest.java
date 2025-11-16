package com.devvault.offline;

import com.devvault.offline.model.Project;
import com.devvault.offline.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OfflineModuleSmokeTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void contextLoadsAndSavesProject() {
        Project p = new Project("offline-project-1");
        Project saved = projectRepository.save(p);
        assertThat(projectRepository.findById(Objects.requireNonNull(saved.getId()))).isPresent();
        assertThat(projectRepository.findById(saved.getId())).isPresent();
    }
}
