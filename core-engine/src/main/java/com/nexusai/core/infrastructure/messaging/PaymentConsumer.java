package com.nexusai.core.infrastructure.messaging;

import com.nexusai.core.domain.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final BillingService billingService;

    @KafkaListener(topics = "payment-events", groupId = "nexusai-group")
    public void listen(String message) {
        String[] parts = message.split(",");
        Long userId = Long.parseLong(parts[0]);
        BigDecimal amount = new BigDecimal(parts[1]);
        String reference = parts[2];

        // Aquí es donde finalmente se actualiza la DB después del evento
        billingService.topUpBalance(userId, amount, reference);
    }
}