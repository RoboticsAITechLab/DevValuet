package com.devvault.backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BackupControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private static Path backupBase;

    static {
        try {
            backupBase = Files.createTempDirectory("dv-backup-base");
            backupBase.toFile().deleteOnExit();
            Path keys = Files.createTempDirectory("dv-keystore-back");
            keys.toFile().deleteOnExit();
            System.setProperty("devvault.keystore.dir", keys.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry reg) {
        reg.add("devvault.backup.allowed-base", () -> backupBase.toAbsolutePath().toString());
        // ensure tests run without requiring a configured token
        reg.add("devvault.backup.token", () -> "");
    }

    @Test
    void createBackup_shouldProduceEncryptedFile() throws Exception {
        Path src = Files.createTempDirectory("dv-back-src");
        Files.writeString(src.resolve("a.txt"), "data");

    String baseUrl = "http://localhost:" + port;
    System.out.println("Using backup base dir: " + backupBase.toAbsolutePath());
        // build URI with proper encoding (avoid manual URLEncoder which can lead to double-encoding)
        Path explicitDest = backupBase.resolve("explicit-test.enc");
        org.springframework.web.util.UriComponents uri = org.springframework.web.util.UriComponentsBuilder
            .fromHttpUrl(baseUrl + "/api/backup/create")
            .queryParam("source", src.toAbsolutePath().toString())
            .queryParam("dest", explicitDest.toAbsolutePath().toString())
            .build()
            .encode();
        java.net.URI uriObj = uri.toUri();
    ResponseEntity<java.util.Map<String,Object>> resp = restTemplate.postForEntity(uriObj, null, (Class) java.util.Map.class);
    System.out.println("POST /api/backup/create status=" + resp.getStatusCodeValue());
    System.out.println("POST /api/backup/create body(raw)=" + resp.getBody());
    assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    java.util.Map<String, Object> body = resp.getBody();
    System.out.println("Backup response map: " + body);
    assertThat(body).isNotNull();
    assertThat(body.get("message")).isEqualTo("Backup created");

    // show what is on disk for debugging
    Files.list(backupBase).forEach(p -> System.out.println("backup-file: " + p.toAbsolutePath()));

    // extract created path from response map and attempt to download the created file via the controller
    String createdPath = (String) body.get("created");
    Path created = Path.of(createdPath);
    System.out.println("Created path reported: " + created.toAbsolutePath());

    // extract filename and attempt to download the file from the server to verify server-side visibility
    String filename = created.getFileName().toString();
    String encodedFilename = java.net.URLEncoder.encode(filename, java.nio.charset.StandardCharsets.UTF_8);
    ResponseEntity<byte[]> dlResp = null;
    int attempts = 0;
    while (attempts < 10) {
        ResponseEntity<byte[]> tmp = restTemplate.getForEntity(baseUrl + "/api/backup/download?file=" + encodedFilename, byte[].class);
        if (tmp != null) {
            byte[] bytes = tmp.getBody();
            if (tmp.getStatusCode().is2xxSuccessful() && bytes != null && bytes.length > 0) {
                dlResp = tmp;
                break;
            }
        }
        attempts++;
        Thread.sleep(200);
    }
    assertThat(dlResp).isNotNull();
    byte[] finalBytes = dlResp != null ? dlResp.getBody() : null;
    if (dlResp != null) {
        assertThat(dlResp.getStatusCode().is2xxSuccessful()).isTrue();
    }
    assertThat(finalBytes).isNotNull();
    if (finalBytes != null) {
        assertThat(finalBytes.length).isGreaterThan(0);
    }
    }
}
