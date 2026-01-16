package com.nexusai.core.domain.service;

import com.nexusai.core.domain.repository.PaymentGateway;
import com.nexusai.core.domain.repository.TransactionRepository;
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.persistence.entities.TransactionEntity;
import com.nexusai.core.infrastructure.persistence.entities.TransactionType;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentGateway paymentGateway; // Inyectamos la interfaz

    /**
     * Inicia el proceso de recarga generando la URL de Stripe
     */
    public String initiateReload(Long userId, BigDecimal amount) {
        // Podrías añadir validaciones aquí (ej. monto mínimo)
        return paymentGateway.createPaymentUrl(userId, amount);
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
                .description("Consulta Gemini 1.5 Flash")
                .build();
        
        transactionRepository.save(transaction);
    }

    @Transactional
    public void topUpBalance(Long userId, BigDecimal amount, String stripeId) {
        WalletEntity wallet = walletRepository.findByUserId(userId);
        if (wallet == null) throw new RuntimeException("Billetera no encontrada");

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        TransactionEntity transaction = TransactionEntity.builder()
                .wallet(wallet)
                .amount(amount)
                .type(TransactionType.CREDIT)
                .description("Recarga exitosa. Stripe ID: " + stripeId)
                .build();

        transactionRepository.save(transaction);
    }
}