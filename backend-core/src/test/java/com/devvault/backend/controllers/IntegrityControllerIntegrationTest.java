package com.devvault.backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrityControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Path keysDir;

    static {
        try {
            keysDir = Files.createTempDirectory("dv-keystore-int");
            keysDir.toFile().deleteOnExit();
            System.setProperty("devvault.keystore.dir", keysDir.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void scanAndVerifyEndpoints_shouldReturnOk() throws IOException {
        Path temp = Files.createTempDirectory("dv-int-test");
        // create a small test file
        Path f = temp.resolve("test.txt");
        Files.writeString(f, "hello world");

        String baseUrl = "http://localhost:" + port;

        // call scan
    ResponseEntity<Map<String,Object>> scanResp = restTemplate.postForEntity(baseUrl + "/api/v1/integrity/scan?path=" + temp.toAbsolutePath(), null, (Class) Map.class);
    assertThat(scanResp.getStatusCode().is2xxSuccessful()).isTrue();
    Map<String,Object> body = scanResp.getBody();
    assertThat(body).isNotNull();
    assertThat(body.get("entries")).isNotNull();

        // call verify
    ResponseEntity<Map<String,Object>> verifyResp = restTemplate.getForEntity(baseUrl + "/api/v1/integrity/verify?path=" + temp.toAbsolutePath(), (Class) Map.class);
    assertThat(verifyResp.getStatusCode().is2xxSuccessful()).isTrue();
    Map<String,Object> verifyBody = verifyResp.getBody();
    assertThat(verifyBody).isNotNull();
    // result should contain our file
    boolean found = verifyBody.keySet().stream().anyMatch(k -> k.toString().contains("test.txt"));
        assertThat(found).isTrue();
    }
}
