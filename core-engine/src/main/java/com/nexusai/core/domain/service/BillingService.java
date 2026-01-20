package com.nexusai.core.domain.service;

import com.nexusai.core.domain.repository.TransactionRepository;
import com.nexusai.core.domain.repository.UserRepository;
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.billing.StripeAdapter;
import com.nexusai.core.infrastructure.messaging.PaymentProducer;
import com.nexusai.core.infrastructure.persistence.entities.TransactionEntity;
import com.nexusai.core.infrastructure.persistence.entities.TransactionType;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import com.stripe.model.checkout.Session;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
public class BillingService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final StripeAdapter stripeAdapter;
    private final PaymentProducer paymentProducer;
    private final Counter totalPaymentsCounter;

    public BillingService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            StripeAdapter stripeAdapter,
            PaymentProducer paymentProducer,
            MeterRegistry meterRegistry) {
        
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.stripeAdapter = stripeAdapter;
        this.paymentProducer = paymentProducer;

        this.totalPaymentsCounter = Counter.builder("nexusai.payments.total")
                .description("Total acumulado de ingresos por recargas de saldo")
                .tag("service", "billing")
                .register(meterRegistry);
    }

    public String initiateReload(Long userId, BigDecimal amount) {
        log.info("--- 💳 Iniciando recarga de {} para usuario ID: {}", amount, userId);
        // Usamos el nombre del método corregido en el StripeAdapter
        return stripeAdapter.createCheckoutSession(userId, amount);
    }

    public void completeReload(String sessionId) {
        try {
            Session session = stripeAdapter.retrieveSession(sessionId);
            Long userId = Long.parseLong(session.getMetadata().get("userId"));
            
            BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal()).divide(new BigDecimal("100"));

            log.info("--- 💸 Pago de Stripe verificado. Enviando a Kafka...");
            paymentProducer.sendPaymentEvent(userId, amount.toString(), sessionId);
            totalPaymentsCounter.increment(amount.doubleValue());

        } catch (Exception e) {
            log.error("--- ❌ Error al procesar el éxito del pago: {}", e.getMessage());
            throw new RuntimeException("Error en el flujo de facturación");
        }
    }

    @Transactional
    public void topUpBalance(Long userId, BigDecimal amount, String reference) {
        WalletEntity wallet = walletRepository.findByUserId(userId);
        if (wallet == null) throw new RuntimeException("Billetera no encontrada");

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        TransactionEntity tx = TransactionEntity.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.RELOAD)
                .externalReference(reference)
                .description("Stripe Top-up") // Agregado para cumplir con nullable=false
                .createdAt(LocalDateTime.now())
                .build();
        
        transactionRepository.save(tx);
        log.info("--- ✅ Billetera actualizada: Usuario {} ahora tiene {}", userId, wallet.getBalance());
    }

    @Transactional
    public void processAiUsage(Long userId, BigDecimal cost) {
        WalletEntity wallet = walletRepository.findByUserId(userId);
        
        if (wallet == null || wallet.getBalance().compareTo(cost) < 0) {
            log.warn("--- 🚫 Usuario {} intentó usar IA sin saldo suficiente", userId);
            throw new RuntimeException("Saldo insuficiente para procesar la petición de IA");
        }

        wallet.setBalance(wallet.getBalance().subtract(cost));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        TransactionEntity tx = TransactionEntity.builder()
                .wallet(wallet)
                .amount(cost.negate())
                .type(TransactionType.AI_USAGE)
                .description("Consumo AI Orchestrator") // Agregado
                .createdAt(LocalDateTime.now())
                .build();
        
        transactionRepository.save(tx);
        log.info("--- 🤖 IA consumida. Saldo restante para usuario {}: {}", userId, wallet.getBalance());
    }
}