package com.toolschallenge.payments.service;

import com.toolschallenge.payments.domain.PaymentDescription;
import com.toolschallenge.payments.domain.PaymentTransaction;
import com.toolschallenge.payments.domain.TransactionStatus;
import com.toolschallenge.payments.exception.DuplicateTransactionException;
import com.toolschallenge.payments.exception.InvalidRefundException;
import com.toolschallenge.payments.exception.TransactionNotFoundException;
import com.toolschallenge.payments.repository.PaymentRepository;
import com.toolschallenge.payments.strategy.PaymentAuthorizationStrategy;
import com.toolschallenge.payments.strategy.PaymentAuthorizationStrategyFactory;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAuthorizationStrategyFactory strategyFactory;

    public PaymentService(PaymentRepository paymentRepository, PaymentAuthorizationStrategyFactory strategyFactory) {
        this.paymentRepository = paymentRepository;
        this.strategyFactory = strategyFactory;
    }

    public PaymentTransaction create(PaymentTransaction request) {
        paymentRepository.findById(request.id()).ifPresent(existing -> {
            throw new DuplicateTransactionException(request.id());
        });

        PaymentAuthorizationStrategy strategy = strategyFactory.getStrategy(request.paymentMethod().type());
        TransactionStatus status = strategy.authorize(request);

        PaymentTransaction savedTransaction = enrich(request, status);
        return paymentRepository.save(savedTransaction);
    }

    public PaymentTransaction refund(String id) {
        PaymentTransaction current = getById(id);
        if (current.description().status() == TransactionStatus.CANCELADO) {
            throw new InvalidRefundException(id);
        }

        PaymentTransaction canceledTransaction = current.withDescription(current.description().withStatus(TransactionStatus.CANCELADO));
        return paymentRepository.save(canceledTransaction);
    }

    public List<PaymentTransaction> getAll() {
        return paymentRepository.findAll();
    }

    public PaymentTransaction getById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    private PaymentTransaction enrich(PaymentTransaction request, TransactionStatus status) {
        PaymentDescription description = request.description().withNsu(generateNumericCode(10))
                .withAuthorizationCode(generateNumericCode(9))
                .withStatus(status);

        return request.withDescription(description);
    }

    private String generateNumericCode(int length) {
        StringBuilder builder = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int index = 0; index < length; index++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
