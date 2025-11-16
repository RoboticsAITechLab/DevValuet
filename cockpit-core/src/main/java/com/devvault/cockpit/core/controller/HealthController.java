package com.devvault.cockpit.core.controller;

import com.devvault.cockpit.core.git.GitResult;
import com.devvault.cockpit.core.git.GitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.Duration;

@RestController
public class HealthController {

    @Autowired
    private GitService gitService;

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(new HealthStatus("Aegis-2100", "ok"));
    }

    @PostMapping("/git/clone")
    public ResponseEntity<?> cloneRepo(@RequestParam String uri, @RequestParam String targetDir) {
        try {
            GitResult r = gitService.cloneRepository(uri, new File(targetDir), Duration.ofMinutes(2));
            if (r.success()) return ResponseEntity.ok().body("cloned");
            return ResponseEntity.status(500).body(r.getError());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/git/status")
    public ResponseEntity<?> gitStatus(@RequestParam String repoDir) {
        try {
            GitResult r = gitService.status(new File(repoDir), Duration.ofSeconds(10));
            if (r.success()) return ResponseEntity.ok().body(r.getOutput());
            return ResponseEntity.status(500).body(r.getError());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    static class HealthStatus {
        public String name;
        public String status;
        public HealthStatus(String n, String s) { this.name = n; this.status = s; }
    }
}
