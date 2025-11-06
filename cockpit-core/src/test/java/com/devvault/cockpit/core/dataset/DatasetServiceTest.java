package com.devvault.cockpit.core.dataset;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatasetServiceTest {

    @Test
    public void testExtractMetadata() throws Exception {
        var tmp = Files.createTempDirectory("ds-test");
        File d1 = tmp.resolve("a.txt").toFile();
        Files.writeString(d1.toPath(), "hello");
        File subdir = tmp.resolve("sub").toFile();
        subdir.mkdirs();
        File d2 = new File(subdir, "b.txt");
        Files.writeString(d2.toPath(), "world");

        Dataset ds = new Dataset("t", tmp.toAbsolutePath().toString(), 1L);
        DatasetService s = new DatasetService();
        DatasetMetadata meta = s.extractMetadata(ds);
        assertNotNull(meta);
        assertEquals(2, meta.getFileCount());
        assertTrue(meta.getTotalSizeBytes() >= 10);
        assertNotNull(meta.getSampleFiles());
        assertTrue(meta.getSampleFiles().size() >= 1);
    }
}
