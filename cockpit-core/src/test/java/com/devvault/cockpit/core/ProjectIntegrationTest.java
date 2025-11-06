package com.devvault.cockpit.core;

import com.devvault.cockpit.core.project.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createAndGetProject() {
        String base = "http://localhost:" + port + "/api/projects";

        Project p = new Project("Test Project", "desc", "java,ai");
        ResponseEntity<Project> created = restTemplate.postForEntity(base, p, Project.class);
        assertThat(created.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(created.getBody()).isNotNull();
        Long id = created.getBody().getId();
        assertThat(id).isNotNull();

        ResponseEntity<Project> fetched = restTemplate.getForEntity(base + "/" + id, Project.class);
        assertThat(fetched.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(fetched.getBody()).isNotNull();
        assertThat(fetched.getBody().getName()).isEqualTo("Test Project");

        // Delete
        restTemplate.delete(base + "/" + id);
        ResponseEntity<Project> afterDelete = restTemplate.getForEntity(base + "/" + id, Project.class);
        assertThat(afterDelete.getStatusCode().value()).isEqualTo(404);
    }
}
