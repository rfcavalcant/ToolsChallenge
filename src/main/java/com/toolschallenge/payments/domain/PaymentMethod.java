package com.toolschallenge.payments.domain;

public record PaymentMethod(
        PaymentMethodType type,
        int installments
) {
}
