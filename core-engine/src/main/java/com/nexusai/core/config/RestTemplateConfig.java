package com.nexusai.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate; // Esta es la de Spring

@Configuration
public class RestTemplateConfig { // <-- Cambiamos el nombre aquí

    @Bean
    public RestTemplate restTemplate() { // <-- Ahora Java sabe que este es el de la importación
        return new RestTemplate();
    }
}