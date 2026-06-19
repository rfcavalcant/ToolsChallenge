package com.toolschallenge.payments.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDescription(
        BigDecimal amount,
        LocalDateTime dateTime,
        String establishment,
        String nsu,
        String authorizationCode,
        TransactionStatus status
) {

    public PaymentDescription withNsu(String nsu) {
        return new PaymentDescription(amount, dateTime, establishment, nsu, authorizationCode, status);
    }

    public PaymentDescription withAuthorizationCode(String authorizationCode) {
        return new PaymentDescription(amount, dateTime, establishment, nsu, authorizationCode, status);
    }

    public PaymentDescription withStatus(TransactionStatus status) {
        return new PaymentDescription(amount, dateTime, establishment, nsu, authorizationCode, status);
    }
}
