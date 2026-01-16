package com.nexusai.core.controller.billing;

import com.nexusai.core.controller.dto.PaymentResponse;
import com.nexusai.core.controller.dto.ReloadRequest; // Importante
import com.nexusai.core.domain.service.BillingService; // Importante
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map; // Importante

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class PaymentController {

    private final WalletRepository walletRepository;
    private final BillingService billingService; // Agregado: para que funcione el /reload

    @GetMapping("/balance/{userId}")
    public ResponseEntity<PaymentResponse> getBalance(@PathVariable Long userId) {
        WalletEntity wallet = walletRepository.findByUserId(userId);

        if (wallet == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PaymentResponse(
                userId,
                wallet.getBalance(),
                "Saldo consultado correctamente"));
    }

    @PostMapping("/reload")
    public ResponseEntity<Map<String, String>> createReloadUrl(@RequestBody ReloadRequest request) {
        // Llamamos al servicio para obtener la URL de Stripe
        String url = billingService.initiateReload(request.userId(), request.amount());

        return ResponseEntity.ok(Map.of("url", url));
    }
}