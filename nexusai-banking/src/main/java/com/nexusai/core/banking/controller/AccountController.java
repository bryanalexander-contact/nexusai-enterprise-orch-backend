package com.nexusai.core.banking.controller;

import com.nexusai.core.banking.domain.model.AccountEntity;
import com.nexusai.core.banking.domain.repository.AccountRepository;
import com.nexusai.core.banking.service.StripeService;
import com.nexusai.core.banking.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountRepository accountRepository;
    private final TransferService transferService;
    private final StripeService stripeService;

    @GetMapping
    public ResponseEntity<List<AccountEntity>> getMyAccounts(@RequestParam Long userId) {
        return ResponseEntity.ok(accountRepository.findByUserId(userId));
    }

    @PostMapping("/{accountId}/transfer")
    public ResponseEntity<String> transfer(
            @PathVariable Long accountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount
    ) {
        transferService.transfer(accountId, toAccountId, amount);
        return ResponseEntity.ok("Transferencia exitosa");
    }

    @PostMapping("/{accountId}/recharge")
    public ResponseEntity<String> recharge(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount
    ) throws Exception {
        String clientSecret = stripeService.createPaymentIntent(accountId, amount);
        return ResponseEntity.ok(clientSecret);
    }
}
