package com.devvault.backup;

import java.nio.file.Path;

/**
 * Minimal backup manager skeleton — later will support incremental snapshots and encrypted archives.
 */
public class BackupManager {
    private final Path storageDir;

    public BackupManager(Path storageDir) {
        this.storageDir = storageDir;
    }

    public void createSnapshot() {
        // placeholder: detect changed files, compress, encrypt, and store
        System.out.println("Creating snapshot to " + storageDir);
    }
}
