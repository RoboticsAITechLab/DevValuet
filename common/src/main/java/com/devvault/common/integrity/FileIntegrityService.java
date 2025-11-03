package com.devvault.common.integrity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple file integrity service:
 * - computes SHA-256 hashes for files under a directory (relative paths)
 * - persists an index under user home .devvault/integrity/index.csv
 * - verifies current state against persisted index
 */
public class FileIntegrityService {
    private static final String INDEX_DIR = System.getProperty("user.home") + java.io.File.separator + ".devvault" + java.io.File.separator + "integrity";
    private static final String INDEX_FILE = "index.csv";

    private final Path indexPath;

    public FileIntegrityService() throws IOException {
        Path dir = Path.of(INDEX_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        this.indexPath = dir.resolve(INDEX_FILE);
        if (!Files.exists(indexPath)) Files.createFile(indexPath);
    }

    // For tests and custom paths
    public FileIntegrityService(Path indexPath) throws IOException {
        if (!Files.exists(indexPath.getParent())) Files.createDirectories(indexPath.getParent());
        if (!Files.exists(indexPath)) Files.createFile(indexPath);
        this.indexPath = indexPath;
    }

    public Map<String, String> computeHashes(Path baseDir) throws Exception {
        if (!Files.exists(baseDir) || !Files.isDirectory(baseDir)) throw new IllegalArgumentException("baseDir must be a directory");
        Map<String, String> map = new LinkedHashMap<>();
        Files.walk(baseDir)
                .filter(Files::isRegularFile)
                .forEach(p -> {
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        byte[] data = Files.readAllBytes(p);
                        byte[] digest = md.digest(data);
                        String hex = bytesToHex(digest);
                        String rel = baseDir.relativize(p).toString().replace('\\', '/');
                        map.put(rel, hex);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return map;
    }

    public void saveIndex(Map<String, String> index) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(indexPath, StandardCharsets.UTF_8)) {
            for (Map.Entry<String, String> e : index.entrySet()) {
                w.write(e.getKey().replace('\n', ' ') + "," + e.getValue());
                w.newLine();
            }
        }
    }

    public Map<String, String> loadIndex() throws IOException {
        Map<String, String> map = new LinkedHashMap<>();
        try (BufferedReader r = Files.newBufferedReader(indexPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                int idx = line.lastIndexOf(',');
                if (idx <= 0) continue;
                String path = line.substring(0, idx);
                String hash = line.substring(idx + 1);
                map.put(path, hash);
            }
        }
        return map;
    }

    /**
     * Verify current files under baseDir against saved index. Returns map of relative path -> true if matches, false otherwise.
     */
    public Map<String, Boolean> verify(Path baseDir) throws Exception {
        Map<String, String> current = computeHashes(baseDir);
        Map<String, String> saved = loadIndex();
        Map<String, Boolean> result = new LinkedHashMap<>();

        // check saved entries
        for (Map.Entry<String, String> e : saved.entrySet()) {
            String path = e.getKey();
            String savedHash = e.getValue();
            String cur = current.get(path);
            result.put(path, cur != null && cur.equals(savedHash));
        }
        // include new files not in saved index
        for (Map.Entry<String, String> e : current.entrySet()) {
            if (!saved.containsKey(e.getKey())) result.put(e.getKey(), false);
        }
        return result;
    }

    public Path getIndexPath() {
        return indexPath;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
