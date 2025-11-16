package com.devvault.offline.cli;

import com.devvault.offline.dto.SnapshotExport;
import com.devvault.offline.service.SnapshotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SnapshotCli {

    private final SnapshotService snapshotService;
    private final ObjectMapper mapper;

    public SnapshotCli(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public void exportTo(Path outFile) throws IOException {
        SnapshotExport export = snapshotService.exportSnapshot();
        String json = mapper.writeValueAsString(export);
        Files.writeString(outFile, json);
    }

    public SnapshotExport importFrom(Path inFile) throws IOException {
        String json = Files.readString(inFile);
        SnapshotExport snap = mapper.readValue(json, SnapshotExport.class);
        snapshotService.importSnapshot(snap);
        return snap;
    }
}
