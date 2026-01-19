package com.nexusai.core.controller.billing;

import com.nexusai.core.controller.dto.PaymentResponse;
import com.nexusai.core.controller.dto.ReloadRequest;
import com.nexusai.core.domain.service.BillingService;
import com.nexusai.core.domain.repository.WalletRepository;
import com.nexusai.core.infrastructure.persistence.entities.WalletEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class PaymentController {

    private final WalletRepository walletRepository;
    private final BillingService billingService;

    @GetMapping("/balance")
    public ResponseEntity<PaymentResponse> getBalance() {
        // Obtenemos el ID del usuario directamente del Token autenticado
        Long userId = getCurrentUserId();
        
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
        // Validamos que el ID del token coincida con el de la petición o usamos el del token
        Long userId = getCurrentUserId();
        String url = billingService.initiateReload(userId, request.amount());
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("session_id") String sessionId) {
        billingService.completeReload(sessionId);
        return ResponseEntity.ok("<h1>¡Pago Exitoso!</h1><p>NexusAI ha procesado tu pago a través de Kafka.</p>");
    }

    /**
     * Método utilitario para extraer el usuario del token actual
     */
    private Long getCurrentUserId() {
        // Aquí asumimos que guardaste el ID en el 'subject' o 'claims' del JWT
        // Por ahora devolvemos el principal (normalmente el email o ID)
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(principal);
    }
}