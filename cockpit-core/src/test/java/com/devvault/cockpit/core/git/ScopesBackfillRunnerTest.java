package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ScopesBackfillRunnerTest {

    @Test
    public void dryRunDoesNotPersist() throws Exception {
        GitHubConnectionRepository connRepo = mock(GitHubConnectionRepository.class);
        GitHubConnectionScopeRepository scopeRepo = mock(GitHubConnectionScopeRepository.class);

        GitHubConnection c = new GitHubConnection();
        c.setId(7L);
        c.setScopes("repo, user:email");

        when(connRepo.findAll()).thenReturn(List.of(c));

        ScopesBackfillRunner runner = new ScopesBackfillRunner(connRepo, scopeRepo);
        runner.setDryRun(true);
        runner.run();

        verify(scopeRepo, never()).save(any());
        // deleteByConnectionId should not be called in dry-run
        verify(scopeRepo, never()).deleteByConnectionId(anyLong());
    }

    @Test
    public void liveRunSavesScopes() throws Exception {
        GitHubConnectionRepository connRepo = mock(GitHubConnectionRepository.class);
        GitHubConnectionScopeRepository scopeRepo = mock(GitHubConnectionScopeRepository.class);

        GitHubConnection c = new GitHubConnection();
        c.setId(8L);
        c.setScopes("repo, user:email");

        when(connRepo.findAll()).thenReturn(List.of(c));

        ScopesBackfillRunner runner = new ScopesBackfillRunner(connRepo, scopeRepo);
        runner.setDryRun(false);
        runner.run();

        verify(scopeRepo, times(1)).deleteByConnectionId(8L);
        // verify save called at least once
        verify(scopeRepo, atLeast(1)).save(any(GitHubConnectionScope.class));
    }
}
