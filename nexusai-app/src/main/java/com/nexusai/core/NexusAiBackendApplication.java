package com.nexusai.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.nexusai.core")
public class NexusAiBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(NexusAiBackendApplication.class, args);
    }
}