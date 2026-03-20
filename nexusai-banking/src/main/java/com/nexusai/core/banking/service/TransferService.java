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
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        AccountEntity source = accountRepository.findById(fromAccountId).orElseThrow();
        AccountEntity destination = accountRepository.findById(toAccountId).orElseThrow();

        if (source.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Saldo insuficiente");
        }

        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));

        accountRepository.save(source);
        accountRepository.save(destination);

        transactionRepository.save(TransactionEntity.builder()
                .accountId(fromAccountId)
                .amount(amount.negate())
                .type("TRANSFER_OUT")
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build());

        transactionRepository.save(TransactionEntity.builder()
                .accountId(toAccountId)
                .amount(amount)
                .type("TRANSFER_IN")
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .build());
    }
}
