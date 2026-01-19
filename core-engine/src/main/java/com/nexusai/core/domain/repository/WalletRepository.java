package com.nexusai.core.domain.repository;

import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    // Buscaremos la billetera por el ID del usuario
    WalletEntity findByUserId(Long userId);
}