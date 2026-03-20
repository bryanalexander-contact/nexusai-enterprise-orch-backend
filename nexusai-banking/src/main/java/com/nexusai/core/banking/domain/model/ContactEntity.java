package com.nexusai.core.banking.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contacts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String beneficiaryName;
    private String accountNumber;
    private String alias;
}
