package com.devvault.cockpit.core.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DefaultCommandRunner implements CommandRunner {
    private static final Logger log = LoggerFactory.getLogger(DefaultCommandRunner.class);

    @Override
    public GitResult run(File dir, Duration timeout, String... cmd) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (dir != null) pb.directory(dir);
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            String out;
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                out = r.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            boolean finished = p.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            int rc = finished ? p.exitValue() : -1;
            if (!finished) {
                p.destroyForcibly();
                return new GitResult(-1, out, "timeout");
            }
            return new GitResult(rc, out, rc == 0 ? "" : out);
        } catch (Exception e) {
            log.error("command run error", e);
            return new GitResult(-2, "", e.getMessage());
        }
    }
}
