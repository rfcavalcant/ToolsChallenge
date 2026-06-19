package com.toolschallenge.payments.unit.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.TransactionStatus;
import com.toolschallenge.payments.strategy.CashPaymentAuthorizationStrategy;
import com.toolschallenge.payments.testsupport.PaymentTransactionFixture;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CashPaymentAuthorizationStrategyTest {

    private final CashPaymentAuthorizationStrategy strategy = new CashPaymentAuthorizationStrategy();

    @Test
    void shouldAuthorizeCashPaymentWhenAmountIsPositiveAndInstallmentsIsOne() {
        var transaction = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "1", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 1
        );

        assertThat(strategy.authorize(transaction)).isEqualTo(TransactionStatus.AUTORIZADO);
    }

    @Test
    void shouldDenyCashPaymentWhenInstallmentsIsDifferentFromOne() {
        var transaction = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "1", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 2
        );

        assertThat(strategy.authorize(transaction)).isEqualTo(TransactionStatus.NEGADO);
    }
}
