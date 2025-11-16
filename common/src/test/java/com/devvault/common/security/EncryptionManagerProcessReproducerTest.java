package com.devvault.common.security;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EncryptionManagerProcessReproducerTest {

    @Test
    public void twoProcessRotationReproducer() throws Exception {
        Path tmpHome = Files.createTempDirectory("dv-repro-");
        tmpHome.toFile().deleteOnExit();

        String pass = "repro-pass";

        String javaBin = System.getProperty("java.home") + "/bin/java";
        String cp = System.getProperty("java.class.path");

        // writer
        List<String> writerCmd = new ArrayList<>();
        writerCmd.add(javaBin);
        writerCmd.add("-cp"); writerCmd.add(cp);
        writerCmd.add("com.devvault.common.security.EncryptionManagerProcessHelper");
        writerCmd.add("writer");
        writerCmd.add(tmpHome.toAbsolutePath().toString());
        writerCmd.add(pass);

        ProcessBuilder pw = new ProcessBuilder(writerCmd);
        pw.inheritIO();
        Process p1 = pw.start();
        int wexit = p1.waitFor();
    assertEquals(0, wexit);

        // reader
        List<String> readerCmd = new ArrayList<>();
        readerCmd.add(javaBin);
        readerCmd.add("-cp"); readerCmd.add(cp);
        readerCmd.add("com.devvault.common.security.EncryptionManagerProcessHelper");
        readerCmd.add("reader");
        readerCmd.add(tmpHome.toAbsolutePath().toString());
        readerCmd.add(pass);

        ProcessBuilder pr = new ProcessBuilder(readerCmd);
        pr.inheritIO();
        Process p2 = pr.start();
        int rexit = p2.waitFor();
    assertEquals(0, rexit);
    }
}
