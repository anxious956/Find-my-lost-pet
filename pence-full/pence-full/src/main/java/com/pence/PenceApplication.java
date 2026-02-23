package com.pence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

@SpringBootApplication
public class PenceApplication {

    private static final Logger log = Logger.getLogger(PenceApplication.class.getName());

    public static void main(String[] args) throws Exception {

        // Upload qovluqlarını yarat
        Files.createDirectories(Paths.get("uploads/lost"));
        Files.createDirectories(Paths.get("uploads/found"));

        SpringApplication.run(PenceApplication.class, args);

        log.info("""
                ╔══════════════════════════════════════╗
                ║  🐾 PƏNCƏ — Backend hazırdır!       ║
                ║  http://localhost:8080               ║
                ╚══════════════════════════════════════╝
                """);
    }
}
