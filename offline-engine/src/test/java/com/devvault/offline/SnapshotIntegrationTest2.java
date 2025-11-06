package com.devvault.offline;

import com.devvault.offline.dto.SnapshotExport;
import com.devvault.offline.model.Project;
import com.devvault.offline.service.SnapshotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SnapshotIntegrationTest2 {

    @Autowired
    SnapshotService snapshotService;

    @Test
    @Transactional
    public void importAndExportRoundtrip() throws Exception {
        Project p = new Project();
        p.setName("integration-project-2");

        SnapshotExport in = new SnapshotExport(Collections.singletonList(p), null, null);
        snapshotService.importSnapshot(in);

        SnapshotExport export = snapshotService.exportSnapshot();
        assertThat(export).isNotNull();
        assertThat(export.projects).isNotEmpty();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(export);
        SnapshotExport round = mapper.readValue(json, SnapshotExport.class);

        assertThat(round.projects).isNotEmpty();
    }
}
