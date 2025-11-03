package com.devvault.backend.controllers;

import com.devvault.backend.integrity.IntegrityManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/integrity")
public class IntegrityController {
    private final IntegrityManagerService integrityManagerService;

    @Autowired
    public IntegrityController(IntegrityManagerService integrityManagerService) {
        this.integrityManagerService = integrityManagerService;
    }

    @PostMapping("/scan")
    public ResponseEntity<?> scanAndSave(@RequestParam(name = "path", required = false) String path) {
        try {
            Path base = (path == null || path.isBlank()) ? Paths.get(System.getProperty("user.home")) : Paths.get(path);
            Map<String, String> hashes = integrityManagerService.computeHashes(base);
            integrityManagerService.saveIndex(hashes);
            return ResponseEntity.ok(Map.of("scanned", base.toAbsolutePath().toString(), "entries", hashes.size(), "index", integrityManagerService.getIndexPath().toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam(name = "path", required = false) String path) {
        try {
            Path base = (path == null || path.isBlank()) ? Paths.get(System.getProperty("user.home")) : Paths.get(path);
            Map<String, Boolean> result = integrityManagerService.verify(base);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
