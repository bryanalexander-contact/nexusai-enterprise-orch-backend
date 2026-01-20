package com.nexusai.core.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    @Column(nullable = false)
    private BigDecimal amount; 

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type; 

    @Column // Se quitó nullable=false por si no siempre hay referencia
    private String externalReference; // Para guardar el ID de sesión de Stripe

    @Column
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}