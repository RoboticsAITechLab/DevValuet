package com.devvault.common.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EncryptionManagerProcessHelper {
    // Usage: java ... EncryptionManagerProcessHelper writer <homeDir> <pass>
    //        java ... EncryptionManagerProcessHelper reader <homeDir> <pass>
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: writer|reader <homeDir> <pass>");
            System.exit(3);
        }
        String mode = args[0];
        Path home = Path.of(args[1]);
        String pass = args[2];
        System.setProperty("user.home", home.toAbsolutePath().toString());

        if ("writer".equals(mode)) {
            EncryptionManager mgr = new EncryptionManager(pass);
            byte[] cipher = mgr.encrypt("before-rotate".getBytes());
            // rotate keys
            mgr.rotateKeys(pass);
            // persist cipher to file for reader
            Path out = home.resolve("test-cipher.bin");
            Files.write(out, cipher, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("writer: wrote cipher bytes to " + out);
            System.exit(0);
        } else if ("reader".equals(mode)) {
            // reader runs in fresh JVM but same user.home
            EncryptionManager mgr = new EncryptionManager(pass);
            Path in = home.resolve("test-cipher.bin");
            byte[] cipher = Files.readAllBytes(in);
            byte[] plain = mgr.decrypt(cipher);
            String s = new String(plain);
            System.out.println("reader: decrypted -> " + s);
            if ("before-rotate".equals(s)) System.exit(0);
            else System.exit(2);
        } else {
            System.err.println("Unknown mode: " + mode);
            System.exit(4);
        }
    }
}
