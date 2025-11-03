package com.devvault.backend.integrity;

import com.devvault.common.integrity.FileIntegrityService;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Map;

@Service
public class IntegrityManagerService {
    private final FileIntegrityService delegate;

    public IntegrityManagerService() {
        try {
            this.delegate = new FileIntegrityService();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize FileIntegrityService", e);
        }
    }

    // compute hashes (does not persist unless caller calls saveIndex)
    public Map<String, String> computeHashes(Path baseDir) throws Exception {
        return delegate.computeHashes(baseDir);
    }

    public void saveIndex(Map<String, String> index) throws Exception {
        delegate.saveIndex(index);
    }

    public Map<String, Boolean> verify(Path baseDir) throws Exception {
        return delegate.verify(baseDir);
    }

    public Path getIndexPath() {
        return delegate.getIndexPath();
    }
}
