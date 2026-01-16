package com.nexusai.core.config;

import com.nexusai.core.domain.repository.UserRepository;
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.persistence.entities.UserEntity;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // 1. Crear usuario de prueba
            UserEntity user = new UserEntity();
            user.setEmail("test@nexusai.com");
            user = userRepository.save(user);

            // 2. Crear billetera con 10.00 USD de regalo
            WalletEntity wallet = new WalletEntity();
            wallet.setUser(user);
            wallet.setBalance(new BigDecimal("10.00"));
            walletRepository.save(wallet);

            System.out.println(">>>> Base de datos inicializada: Usuario ID " + user.getId() + " creado con $10.00");
        }
    }
}