package com.toolschallenge.payments.repository;

import com.toolschallenge.payments.domain.PaymentTransaction;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final ConcurrentHashMap<String, PaymentTransaction> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<PaymentTransaction> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<PaymentTransaction> findAll() {
        return storage.values().stream()
                .sorted((left, right) -> left.id().compareTo(right.id()))
                .toList();
    }

    @Override
    public PaymentTransaction save(PaymentTransaction transaction) {
        storage.put(transaction.id(), transaction);
        return transaction;
    }

    @Override
    public void deleteAll() {
        storage.clear();
    }
}
