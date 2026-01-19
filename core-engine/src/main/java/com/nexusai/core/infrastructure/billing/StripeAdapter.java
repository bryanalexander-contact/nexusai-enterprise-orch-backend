package com.nexusai.core.infrastructure.billing;

import com.nexusai.core.domain.repository.PaymentGateway;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class StripeAdapter implements PaymentGateway {

    @Override
    public String createPaymentUrl(Long userId, BigDecimal amount) {
        try {
            SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                // En producción, estas URLs vendrían de un archivo de configuración
                .setSuccessUrl("http://localhost:8080/api/v1/billing/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:8080/api/v1/billing/cancel")
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount.multiply(new BigDecimal("100")).longValue())
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Recarga de Créditos NexusAI")
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .putMetadata("userId", userId.toString()) 
                .build();

            Session session = Session.create(params);
            return session.getUrl(); 
        } catch (Exception e) {
            throw new RuntimeException("Error al crear sesión de Stripe: " + e.getMessage());
        }
    }
}