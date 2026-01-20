package com.nexusai.core.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "payment-events";

    public void sendPaymentEvent(Long userId, String amount, String reference) {
        // Enviamos un mensaje simple separado por comas o un JSON
        String message = userId + "," + amount + "," + reference;
        kafkaTemplate.send(TOPIC, message);
    }
}