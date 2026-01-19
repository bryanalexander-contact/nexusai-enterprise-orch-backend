package com.nexusai.core.controller.dto;

import java.math.BigDecimal;

public record PaymentResponse(
    Long userId,
    BigDecimal currentBalance,
    String message
) {}