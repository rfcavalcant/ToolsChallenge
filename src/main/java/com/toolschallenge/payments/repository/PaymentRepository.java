package com.toolschallenge.payments.repository;

import com.toolschallenge.payments.domain.PaymentTransaction;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Optional<PaymentTransaction> findById(String id);

    List<PaymentTransaction> findAll();

    PaymentTransaction save(PaymentTransaction transaction);

    void deleteAll();
}
