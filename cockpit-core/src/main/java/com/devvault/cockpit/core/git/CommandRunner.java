package com.devvault.cockpit.core.git;

import java.io.File;
import java.time.Duration;

public interface CommandRunner {
    /**
     * Run a command in the given directory (null = current working dir) with a timeout.
     * Returns a GitResult containing exit code, stdout and stderr (merged or separate per implementation).
     */
    GitResult run(File dir, Duration timeout, String... cmd);
}
