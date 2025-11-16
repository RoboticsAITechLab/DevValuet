package com.devvault.cockpit.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CockpitApplication {
    public static void main(String[] args) {
        SpringApplication.run(CockpitApplication.class, args);
    }
}
