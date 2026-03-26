package org.swp391_group4_backend.ecosolution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcosolutionApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcosolutionApplication.class, args);
    }
}

