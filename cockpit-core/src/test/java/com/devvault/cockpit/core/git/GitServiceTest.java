package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GitServiceTest {
    @Test
    public void smoke() {
        GitService s = new GitService(new com.devvault.cockpit.core.git.DefaultCommandRunner());
        assertNotNull(s);
    }
}
