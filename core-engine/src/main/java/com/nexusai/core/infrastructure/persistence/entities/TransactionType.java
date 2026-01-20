package com.nexusai.core.infrastructure.persistence.entities;

public enum TransactionType {
    CREDIT, // Entra dinero
    DEBIT,
    RELOAD,    // Para recargas de saldo
    AI_USAGE // Sale dinero
}