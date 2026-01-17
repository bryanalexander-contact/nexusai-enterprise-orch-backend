package com.nexusai.core.domain.service;

import com.nexusai.core.domain.repository.PaymentGateway;
import com.nexusai.core.domain.repository.TransactionRepository;
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.persistence.entities.TransactionEntity;
import com.nexusai.core.infrastructure.persistence.entities.TransactionType;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGateway paymentGateway;

    public String initiateReload(Long userId, BigDecimal amount) {
        return paymentGateway.createPaymentUrl(userId, amount);
    }

    /**
     * Recupera la sesión de Stripe y confirma el depósito
     */
    @Transactional
    public void completeReload(String sessionId) {
        try {
            // 1. Recuperamos la sesión desde Stripe
            Session session = Session.retrieve(sessionId);
            
            // 2. Extraemos la info que guardamos en StripeAdapter
            Long userId = Long.parseLong(session.getMetadata().get("userId"));
            BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal()).divide(new BigDecimal("100"));

            // 3. Actualizamos la base de datos
            topUpBalance(userId, amount, session.getId());
            
        } catch (Exception e) {
            throw new RuntimeException("Error al confirmar el pago con Stripe: " + e.getMessage());
        }
    }

    @Transactional
    public void processAiUsage(Long userId, BigDecimal cost) {
        WalletEntity wallet = walletRepository.findByUserId(userId);

        if (wallet == null || wallet.getBalance().compareTo(cost) < 0) {
            throw new RuntimeException("Saldo insuficiente.");
        }

        wallet.setBalance(wallet.getBalance().subtract(cost));
        walletRepository.save(wallet);

        TransactionEntity transaction = TransactionEntity.builder()
                .wallet(wallet)
                .amount(cost.negate())
                .type(TransactionType.DEBIT)
                .description("Consulta Gemini 2.5 Flash")
                .build();
        
        transactionRepository.save(transaction);
    }

    @Transactional
    public void topUpBalance(Long userId, BigDecimal amount, String stripeId) {
        WalletEntity wallet = walletRepository.findByUserId(userId);
        if (wallet == null) throw new RuntimeException("Billetera no encontrada para el usuario: " + userId);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        TransactionEntity transaction = TransactionEntity.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .description("Recarga exitosa. Stripe ID: " + stripeId)
                .build();

        transactionRepository.save(transaction);
        System.out.println("💰 SALDO ACTUALIZADO: User " + userId + " ahora tiene +" + amount);
    }
}