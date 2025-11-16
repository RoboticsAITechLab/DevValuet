package com.devvault.offline.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OfflineWireMockTest {
    private static WireMockServer wire;

    @BeforeAll
    public static void start() {
        wire = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wire.start();
        WireMock.configureFor("localhost", wire.port());
        wire.stubFor(get("/api/ping").willReturn(aResponse().withStatus(200).withBody("pong")));
    }

    @AfterAll
    public static void stop() {
        if (wire != null) wire.stop();
    }

    @Test
    public void pingEndpoint_returnsPong() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = new URI("http://localhost:" + wire.port() + "/api/ping");
        HttpRequest req = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, resp.statusCode());
        assertEquals("pong", resp.body());
    }
}
