package com.nexusai.core.banking.domain.repository;

import com.nexusai.core.banking.domain.model.ContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<ContactEntity, Long> {
    List<ContactEntity> findByUserId(Long userId);
}
