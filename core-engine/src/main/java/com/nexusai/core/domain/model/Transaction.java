package com.nexusai.core.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private Long id;
    private Long walletId;
    private BigDecimal amount;
    private String type; // "RECHARGE" o "USAGE"
    private String description;
    private LocalDateTime createdAt;
}