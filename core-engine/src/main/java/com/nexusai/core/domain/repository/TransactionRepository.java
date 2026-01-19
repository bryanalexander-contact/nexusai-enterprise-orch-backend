package com.nexusai.core.domain.repository;

import com.nexusai.core.infrastructure.persistence.entities.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}