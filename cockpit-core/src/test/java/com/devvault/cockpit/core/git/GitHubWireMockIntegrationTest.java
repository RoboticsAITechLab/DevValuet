package com.devvault.cockpit.core.git;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "github.oauth.token-url=http://localhost:8089/login/oauth/access_token",
    "github.api.base-url=http://localhost:8089",
    "github.client.id=test-client",
    "github.client.secret=test-secret"
})
public class GitHubWireMockIntegrationTest {

    private static WireMockServer wire;

    @Autowired
    private GitHubService gitHubService;

    @BeforeAll
    public static void startWire() {
        wire = new WireMockServer(WireMockConfiguration.options().port(8089));
        wire.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    public static void stopWire() {
        if (wire != null) wire.stop();
    }

    @Test
    public void fullExchangeAndUserFetchWorks() throws Exception {
        // stub token exchange
        stubFor(post(urlEqualTo("/login/oauth/access_token"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"wire-token-123\",\"token_type\":\"bearer\"}")
                        .withStatus(200)));

        // stub user
        stubFor(get(urlEqualTo("/user"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withHeader("X-OAuth-Scopes", "repo, user:email")
                        .withBody("{\"login\":\"wire-octocat\", \"email\":\"wire-octocat@example.com\"}")
                        .withStatus(200)));

        String token = gitHubService.exchangeCodeForToken("anycode");
        assertThat(token).isEqualTo("wire-token-123");

        Map<String,String> info = gitHubService.fetchProviderUserAndScopes(token);
        assertThat(info).containsEntry("providerUser", "wire-octocat");
        assertThat(info).containsEntry("providerEmail", "wire-octocat@example.com");
        assertThat(info.get("scopes")).contains("repo");
        assertThat(info.get("scopes")).contains("user:email");
    }
}
