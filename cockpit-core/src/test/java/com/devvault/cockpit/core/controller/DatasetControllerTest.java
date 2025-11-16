package com.devvault.cockpit.core.controller;

import com.devvault.cockpit.core.dataset.Dataset;
import com.devvault.cockpit.core.dataset.DatasetMetadata;
import com.devvault.cockpit.core.dataset.DatasetRepository;
import com.devvault.cockpit.core.dataset.DatasetService;
import com.devvault.cockpit.core.dataset.DatasetController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasetController.class)
public class DatasetControllerTest {

    @Autowired MockMvc mvc;
    @MockBean DatasetRepository repository;
    @MockBean DatasetService datasetService;

    @Test
    public void testMetadataEndpoint() throws Exception {
        Dataset d = new Dataset("n","/tmp", 1L);
        d.setId(5L);
        when(repository.findById(anyLong())).thenReturn(Optional.of(d));
        when(datasetService.extractMetadata(d)).thenReturn(new DatasetMetadata(2, 100, java.util.List.of("/tmp/a","/tmp/b")));

        mvc.perform(get("/api/projects/1/datasets/5/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileCount").value(2))
                .andExpect(jsonPath("$.totalSizeBytes").value(100));
    }
}
