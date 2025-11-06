package com.devvault.cockpit.core.git;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GitHubController.class)
public class GitHubControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GitHubService gitHubService;

    @MockBean
    private GitHubConnectionRepository connectionRepository;

    @MockBean
    private GitHubOAuthStateRepository stateRepository;

    @Test
    public void authorizeGeneratesStateAndUrl() throws Exception {
        when(stateRepository.save(ArgumentMatchers.any())).thenAnswer(inv -> inv.getArgument(0));
    when(gitHubService.buildAuthorizeUrl(ArgumentMatchers.eq(42L), ArgumentMatchers.anyString())).thenReturn("https://example.com/auth?state=abc");

        mvc.perform(get("/api/projects/42/git/github/authorize"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorizeUrl").value("https://example.com/auth?state=abc"))
                .andExpect(jsonPath("$.state").exists());

        verify(stateRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    public void callbackValidatesStateAndPersistsConnection() throws Exception {
        String state = "s-1";
        GitHubOAuthState st = new GitHubOAuthState(55L, state);
        when(stateRepository.findByState(state)).thenReturn(Optional.of(st));
        GitHubConnection saved = new GitHubConnection(55L, null, "ENC()");
        saved.setId(99L);
        when(gitHubService.exchangeCodeForToken("c1")).thenReturn("access-token-xyz");
    when(gitHubService.fetchProviderUserAndScopes("access-token-xyz")).thenReturn(java.util.Map.of("providerUser","octocat","scopes","repo,user","providerEmail","octocat@example.com"));
    when(gitHubService.saveConnection(55L, "octocat", "octocat@example.com", "repo,user", "access-token-xyz")).thenReturn(saved);

    mvc.perform(post("/api/projects/55/git/github/callback").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("code", "c1")
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.projectId").value(55));

        verify(stateRepository, times(1)).findByState(state);
        verify(stateRepository, times(1)).deleteByState(state);
        verify(gitHubService, times(1)).exchangeCodeForToken("c1");
    verify(gitHubService, times(1)).saveConnection(55L, "octocat", "octocat@example.com", "repo,user", "access-token-xyz");
    }
}
