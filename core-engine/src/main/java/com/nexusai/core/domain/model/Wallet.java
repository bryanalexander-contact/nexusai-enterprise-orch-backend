package com.nexusai.core.domain.model;

import lombok.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    private Long id;
    private Long userId;
    private BigDecimal balance;

    // Lógica de negocio: ¿Puede el usuario pagar la petición de IA?
    public boolean canAfford(BigDecimal cost) {
        return balance != null && balance.compareTo(cost) >= 0;
    }
}