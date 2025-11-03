package com.devvault.common.integrity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FileIntegrityServiceTest {
    private Path tmpDir;
    private Path indexFile;

    @AfterEach
    public void cleanup() throws Exception {
        if (tmpDir != null && Files.exists(tmpDir)) Files.walk(tmpDir).sorted((a, b) -> b.compareTo(a)).forEach(p -> p.toFile().delete());
        if (indexFile != null && Files.exists(indexFile)) Files.delete(indexFile);
    }

    @Test
    public void testComputeSaveAndVerify() throws Exception {
        tmpDir = Files.createTempDirectory("fim-src-" + UUID.randomUUID());
        indexFile = tmpDir.resolve("index.csv");

        Path f1 = tmpDir.resolve("a.txt");
        Files.writeString(f1, "hello");

        FileIntegrityService fis = new FileIntegrityService(indexFile);
        Map<String, String> hashes = fis.computeHashes(tmpDir);
        assertTrue(hashes.containsKey("a.txt"));

        fis.saveIndex(hashes);

        // verify should return true for existing file
        Map<String, Boolean> v = fis.verify(tmpDir);
        assertTrue(v.get("a.txt"));

        // modify file
        Files.writeString(f1, "changed");
        Map<String, Boolean> v2 = fis.verify(tmpDir);
        assertFalse(v2.get("a.txt"));
    }
}
