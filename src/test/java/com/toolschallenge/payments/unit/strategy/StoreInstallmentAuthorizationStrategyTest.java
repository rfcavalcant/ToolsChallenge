package com.toolschallenge.payments.unit.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.TransactionStatus;
import com.toolschallenge.payments.strategy.StoreInstallmentAuthorizationStrategy;
import com.toolschallenge.payments.testsupport.PaymentTransactionFixture;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class StoreInstallmentAuthorizationStrategyTest {

    private final StoreInstallmentAuthorizationStrategy strategy = new StoreInstallmentAuthorizationStrategy();

    @Test
    void shouldAuthorizeStoreInstallmentWhenInstallmentsIsBetweenTwoAndTwelve() {
        var transaction = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "1", BigDecimal.valueOf(500.50), PaymentMethodType.PARCELADO_LOJA, 5
        );

        assertThat(strategy.authorize(transaction)).isEqualTo(TransactionStatus.AUTORIZADO);
    }

    @Test
    void shouldDenyStoreInstallmentWhenInstallmentsIsOutsideAllowedRange() {
        var transaction = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "1", BigDecimal.valueOf(500.50), PaymentMethodType.PARCELADO_LOJA, 13
        );

        assertThat(strategy.authorize(transaction)).isEqualTo(TransactionStatus.NEGADO);
    }
}
