package com.toolschallenge.payments.testsupport;

import com.toolschallenge.payments.domain.PaymentDescription;
import com.toolschallenge.payments.domain.PaymentMethod;
import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.PaymentTransaction;
import com.toolschallenge.payments.domain.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PaymentTransactionFixture {

    private PaymentTransactionFixture() {
    }

    public static PaymentTransaction build(String id, BigDecimal amount, PaymentMethodType type, int installments) {
        return new PaymentTransaction(
                "4444********1234",
                id,
                new PaymentDescription(
                        amount,
                        LocalDateTime.of(2021, 5, 1, 18, 30),
                        "PetShop Mundo cao",
                        "1234567890",
                        "147258369",
                        TransactionStatus.AUTORIZADO
                ),
                new PaymentMethod(type, installments)
        );
    }

    public static PaymentTransaction buildWithoutAuthorizationData(String id, BigDecimal amount, PaymentMethodType type, int installments) {
        return new PaymentTransaction(
                "4444********1234",
                id,
                new PaymentDescription(
                        amount,
                        LocalDateTime.of(2021, 5, 1, 18, 30),
                        "PetShop Mundo cao",
                        null,
                        null,
                        null
                ),
                new PaymentMethod(type, installments)
        );
    }
}
