package com.nexusai.core.banking.service;

import com.nexusai.core.banking.domain.model.AccountEntity;
import com.nexusai.core.banking.domain.model.TransactionEntity;
import com.nexusai.core.banking.domain.model.TransactionStatus;
import com.nexusai.core.banking.domain.repository.AccountRepository;
import com.nexusai.core.banking.domain.repository.TransactionRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${STRIPE_API_KEY}")
    private String stripeApiKey;

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String createPaymentIntent(Long accountId, BigDecimal amount) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(new BigDecimal("100")).longValue())
                .setCurrency("usd")
                .putMetadata("accountId", accountId.toString())
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret();
    }

    public void fulfillPayment(String paymentIntentId) throws Exception {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        if ("succeeded".equals(intent.getStatus())) {
            Long accountId = Long.parseLong(intent.getMetadata().get("accountId"));
            BigDecimal amount = new BigDecimal(intent.getAmount()).divide(new BigDecimal("100"));

            AccountEntity account = accountRepository.findById(accountId).orElseThrow();
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);

            transactionRepository.save(TransactionEntity.builder()
                    .accountId(accountId)
                    .amount(amount)
                    .type("STRIPE_RECHARGE")
                    .status(TransactionStatus.COMPLETED)
                    .createdAt(LocalDateTime.now())
                    .build());
        }
    }
}
