package com.toolschallenge.payments.domain;

public record PaymentTransaction(
        String card,
        String id,
        PaymentDescription description,
        PaymentMethod paymentMethod
) {

    public PaymentTransaction withDescription(PaymentDescription description) {
        return new PaymentTransaction(card, id, description, paymentMethod);
    }
}
