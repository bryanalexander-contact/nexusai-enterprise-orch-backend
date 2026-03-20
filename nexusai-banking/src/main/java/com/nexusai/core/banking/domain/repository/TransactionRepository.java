package com.nexusai.core.banking.domain.repository;

import com.nexusai.core.banking.domain.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
