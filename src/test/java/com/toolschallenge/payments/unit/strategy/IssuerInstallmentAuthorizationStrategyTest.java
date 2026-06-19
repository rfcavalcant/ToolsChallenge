package com.toolschallenge.payments.unit.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.TransactionStatus;
import com.toolschallenge.payments.strategy.IssuerInstallmentAuthorizationStrategy;
import com.toolschallenge.payments.testsupport.PaymentTransactionFixture;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class IssuerInstallmentAuthorizationStrategyTest {

    private final IssuerInstallmentAuthorizationStrategy strategy = new IssuerInstallmentAuthorizationStrategy();

    @Test
    void shouldAuthorizeIssuerInstallmentWhenInstallmentsIsBetweenTwoAndTwentyFour() {
        var transaction = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "1", BigDecimal.valueOf(500.50), PaymentMethodType.PARCELADO_EMISSOR, 12
        );

        assertThat(strategy.authorize(transaction)).isEqualTo(TransactionStatus.AUTORIZADO);
    }

    @Test
    void shouldDenyIssuerInstallmentWhenAmountIsZero() {
        var transaction = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "1", BigDecimal.ZERO, PaymentMethodType.PARCELADO_EMISSOR, 12
        );

        assertThat(strategy.authorize(transaction)).isEqualTo(TransactionStatus.NEGADO);
    }
}
