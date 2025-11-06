package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TokenMigrationRunnerTest {

    @Test
    public void dryRunDoesNotPersistChanges() throws Exception {
        GitHubConnectionRepository repo = mock(GitHubConnectionRepository.class);
        GitTokenEncryptor encryptor = mock(GitTokenEncryptor.class);

        GitHubConnection c = new GitHubConnection();
        c.setId(42L);
        c.setAccessToken("plain-token-xyz");

        when(repo.findAll()).thenReturn(List.of(c));
        when(encryptor.isEnabled()).thenReturn(true);
        // decrypt will throw to indicate stored value is plaintext
        when(encryptor.decrypt(anyString())).thenThrow(new RuntimeException("not encrypted"));
        when(encryptor.encrypt("plain-token-xyz")).thenReturn("enc-abc");

        TokenMigrationRunner runner = new TokenMigrationRunner(repo, encryptor);
        runner.setDryRun(true);

        runner.run();

        // verify we did not call save on repo during dry-run
        verify(repo, never()).save(any());
    }
}
