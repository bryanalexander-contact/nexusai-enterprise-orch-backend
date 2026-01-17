package com.nexusai.core.controller.billing;

import com.nexusai.core.controller.dto.PaymentResponse;
import com.nexusai.core.controller.dto.ReloadRequest;
import com.nexusai.core.domain.service.BillingService;
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class PaymentController {

    private final WalletRepository walletRepository;
    private final BillingService billingService;

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
        String url = billingService.initiateReload(request.userId(), request.amount());
        return ResponseEntity.ok(Map.of("url", url));
    }

    /**
     * Endpoint al que Stripe redirige tras un pago exitoso.
     * http://localhost:8080/api/v1/billing/success?session_id=cs_test_...
     */
    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("session_id") String sessionId) {
        billingService.completeReload(sessionId);
        return ResponseEntity.ok("<h1>¡Pago Exitoso!</h1><p>Tu saldo ha sido actualizado en la base de datos de Docker. Ya puedes volver a usar Gemini.</p>");
    }
}