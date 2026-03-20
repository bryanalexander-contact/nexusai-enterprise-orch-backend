package com.nexusai.core.ai.domain.service;

import com.nexusai.core.banking.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AiOrchestrator {

    private final BillingService billingService;
    private final ChatModel chatModel;

    @Transactional
    public String handleSmartQuery(Long userId, String userPrompt) {
        BigDecimal cost = new BigDecimal("0.05");
        billingService.processAiUsage(userId, cost);

        try {
            return chatModel.call(userPrompt);
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar al motor de IA (Gemini): " + e.getMessage());
        }
    }
}
