package com.nexusai.core.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final BillingService billingService;
    private final RestTemplate restTemplate;

    // URL de tu microservicio de Python
    private final String PYTHON_AI_URL = "http://localhost:8000/v1/ai/process";

    @Transactional
    public String handleSmartQuery(Long userId, String userPrompt) {
        // 1. Definir costo
        BigDecimal cost = new BigDecimal("0.05"); 

        // 2. Intentar cobrar (FinOps: Si no hay plata, no hay IA)
        billingService.processAiUsage(userId, cost);

        // 3. Preparar el cuerpo de la petición para Python
        // Coincide con el 'QueryRequest' que creamos en FastAPI
        Map<String, Object> requestToPython = Map.of(
            "user_query", userPrompt,
            "user_id", userId
        );

        // 4. Llamar al microservicio de Python
        try {
            Map<String, Object> response = restTemplate.postForObject(
                PYTHON_AI_URL, 
                requestToPython, 
                Map.class
            );

            // Extraemos la respuesta que viene del campo "response" en main.py
            return (String) response.get("response");
            
        } catch (Exception e) {
            // Si Python falla, lanzamos error para que el @Transactional haga Rollback del cobro
            throw new RuntimeException("El motor de IA en Python no responde: " + e.getMessage());
        }
    }
}