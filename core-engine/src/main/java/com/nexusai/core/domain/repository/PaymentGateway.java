package com.nexusai.core.domain.repository;

import java.math.BigDecimal;

/**
 * Este es un "Puerto". Define QUÉ necesita nuestro negocio, 
 * pero no CÓMO se hace tecnológicamente.
 */
public interface PaymentGateway {
    String createPaymentUrl(Long userId, BigDecimal amount);
}