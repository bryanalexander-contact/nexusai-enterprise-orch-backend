package com.nexusai.core.banking.service;

import com.nexusai.core.banking.domain.model.AccountEntity;
import com.nexusai.core.banking.domain.model.TransactionEntity;
import com.nexusai.core.banking.domain.model.TransactionStatus;
import com.nexusai.core.banking.domain.repository.AccountRepository;
import com.nexusai.core.banking.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void processAiUsage(Long userId, BigDecimal cost) {
        AccountEntity account = accountRepository.findByUserId(userId)
                .stream()
                .filter(a -> a.getCurrency().equals("USD"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cuenta en USD no encontrada"));

        if (!account.canAfford(cost)) {
            throw new RuntimeException("Saldo insuficiente para procesar la consulta de IA");
        }

        account.setBalance(account.getBalance().subtract(cost));
        accountRepository.save(account);

        transactionRepository.save(TransactionEntity.builder()
                .accountId(account.getId())
                .amount(cost.negate())
                .type("AI_USAGE")
                .description("Consulta de IA (Gemini)")
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
