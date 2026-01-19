package com.nexusai.core.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${app.stripe.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        // Esto inicializa el SDK de Stripe con tu llave del .env
        Stripe.apiKey = apiKey;
    }
}