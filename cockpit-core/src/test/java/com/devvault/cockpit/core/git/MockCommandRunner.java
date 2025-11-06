package com.devvault.cockpit.core.git;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Simple mock command runner for unit tests. Register expected command strings and their results.
 */
public class MockCommandRunner implements CommandRunner {
    private final Map<String, GitResult> responses = new HashMap<>();
    private final GitResult defaultResult = new GitResult(0, "", "");

    public void when(String[] cmd, GitResult result) {
        responses.put(join(cmd), result);
    }

    private String join(String[] cmd) {
        StringJoiner sj = new StringJoiner(" ");
        for (String s : cmd) sj.add(s == null ? "" : s);
        return sj.toString();
    }

    @Override
    public GitResult run(File dir, Duration timeout, String... cmd) {
        GitResult r = responses.get(join(cmd));
        return r != null ? r : defaultResult;
    }
}
