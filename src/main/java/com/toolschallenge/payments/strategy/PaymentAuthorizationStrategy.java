package com.toolschallenge.payments.strategy;

import com.toolschallenge.payments.domain.PaymentTransaction;
import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.TransactionStatus;

public interface PaymentAuthorizationStrategy {

    PaymentMethodType supportedType();

    TransactionStatus authorize(PaymentTransaction transaction);
}
