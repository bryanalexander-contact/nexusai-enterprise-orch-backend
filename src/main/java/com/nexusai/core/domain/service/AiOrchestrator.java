package com.nexusai.core.domain.service;
import com.nexusai.core.infrastructure.ai.GeminiService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final BillingService billingService;
    private final GeminiService geminiService; // Crearemos este a continuación

    /**
     * Este es el método principal que usará el controlador.
     * Coordina el cobro y la ejecución de la IA.
     */
    @Transactional
    public String handleSmartQuery(Long userId, String userPrompt) {
        // 1. Definir costo (En el futuro esto puede variar según el modelo)
        BigDecimal cost = new BigDecimal("0.05"); 

        // 2. Intentar cobrar (Si no hay saldo, BillingService lanzará una excepción)
        billingService.processAiUsage(userId, cost);

        // 3. Si el cobro fue exitoso, pedirle a Gemini la respuesta
        String aiResponse = geminiService.generateResponse(userPrompt);

        // 4. Retornar la respuesta final
        return aiResponse;
    }
}