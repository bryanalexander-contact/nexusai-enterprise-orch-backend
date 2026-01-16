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
    private BigDecimal amount; // Positivo para recargas, negativo para consumos

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type; // CREDIT (Recarga) o DEBIT (Gasto IA)

    @Column(nullable = false)
    private String description; // Ej: "Consulta Gemini 1.5 Flash" o "Stripe Top-up"

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}