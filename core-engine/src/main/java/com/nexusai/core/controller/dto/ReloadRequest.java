package com.nexusai.core.controller.dto;

import java.math.BigDecimal;

public record ReloadRequest(
    Long userId,
    BigDecimal amount
) {}