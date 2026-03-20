package com.nexusai.core.banking.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private BigDecimal balance;

    @Column(nullable = false)
    private String currency;

    public boolean canAfford(BigDecimal cost) {
        return balance != null && balance.compareTo(cost) >= 0;
    }
}
