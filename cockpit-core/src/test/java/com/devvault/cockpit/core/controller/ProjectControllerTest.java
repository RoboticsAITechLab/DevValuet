package com.devvault.cockpit.core.controller;

import com.devvault.cockpit.core.project.Project;
import com.devvault.cockpit.core.project.ProjectService;
import com.devvault.cockpit.core.project.dto.ProjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean ProjectService projectService;

    @Test
    public void testListPagination() throws Exception {
        Project p = new Project("p1","d","t");
        p.setId(1L);
        when(projectService.listPage(any())).thenReturn(new PageImpl<>(List.of(p), PageRequest.of(0,1),1));

        mvc.perform(get("/api/projects?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("p1"));
    }

    @Test
    public void testCreateValidation() throws Exception {
        // missing name should produce 400
        String body = "{ \"description\": \"d\", \"tags\": \"t\" }";
        mvc.perform(post("/api/projects").contentType("application/json").content(body))
                .andExpect(status().isBadRequest());
    }
}
