package com.devvault.cockpit.core.git;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class GitUtils {
    public static String readProcessOutput(java.lang.Process p) throws Exception {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
            return r.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
