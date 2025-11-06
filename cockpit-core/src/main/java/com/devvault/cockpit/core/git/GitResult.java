package com.devvault.cockpit.core.git;

public class GitResult {
    private final int exitCode;
    private final String output;
    private final String error;

    public GitResult(int exitCode, String output, String error) {
        this.exitCode = exitCode;
        this.output = output;
        this.error = error;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutput() {
        return output;
    }

    public String getError() {
        return error;
    }

    public boolean success() {
        return exitCode == 0;
    }
}
