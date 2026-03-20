package com.nexusai.core.banking.domain.repository;

import com.nexusai.core.banking.domain.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByUserId(Long userId);
}
