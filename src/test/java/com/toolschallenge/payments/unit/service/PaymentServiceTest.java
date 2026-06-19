package com.toolschallenge.payments.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.TransactionStatus;
import com.toolschallenge.payments.exception.DuplicateTransactionException;
import com.toolschallenge.payments.exception.InvalidRefundException;
import com.toolschallenge.payments.exception.TransactionNotFoundException;
import com.toolschallenge.payments.repository.PaymentRepository;
import com.toolschallenge.payments.service.PaymentService;
import com.toolschallenge.payments.strategy.PaymentAuthorizationStrategy;
import com.toolschallenge.payments.strategy.PaymentAuthorizationStrategyFactory;
import com.toolschallenge.payments.testsupport.PaymentTransactionFixture;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentAuthorizationStrategyFactory strategyFactory;

    @Mock
    private PaymentAuthorizationStrategy strategy;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, strategyFactory);
    }

    @Test
    void shouldCreateAuthorizedTransaction() {
        var request = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "100023568900001", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 1
        );
        when(paymentRepository.findById(request.id())).thenReturn(Optional.empty());
        when(strategyFactory.getStrategy(PaymentMethodType.AVISTA)).thenReturn(strategy);
        when(strategy.authorize(request)).thenReturn(TransactionStatus.AUTORIZADO);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = paymentService.create(request);

        assertThat(response.description().status()).isEqualTo(TransactionStatus.AUTORIZADO);
        assertThat(response.description().nsu()).hasSize(10);
        assertThat(response.description().authorizationCode()).hasSize(9);
        verify(paymentRepository).save(any());
    }

    @Test
    void shouldCreateDeniedTransactionWhenStrategyRejects() {
        var request = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "100023568900001", BigDecimal.ZERO, PaymentMethodType.AVISTA, 1
        );
        when(paymentRepository.findById(request.id())).thenReturn(Optional.empty());
        when(strategyFactory.getStrategy(PaymentMethodType.AVISTA)).thenReturn(strategy);
        when(strategy.authorize(request)).thenReturn(TransactionStatus.NEGADO);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = paymentService.create(request);

        assertThat(response.description().status()).isEqualTo(TransactionStatus.NEGADO);
    }

    @Test
    void shouldRejectDuplicatedTransactionId() {
        var request = PaymentTransactionFixture.buildWithoutAuthorizationData(
                "100023568900001", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 1
        );
        when(paymentRepository.findById(request.id())).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> paymentService.create(request))
                .isInstanceOf(DuplicateTransactionException.class);
    }

    @Test
    void shouldRefundExistingTransaction() {
        var existing = PaymentTransactionFixture.build(
                "100023568900001", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 1
        );
        when(paymentRepository.findById(existing.id())).thenReturn(Optional.of(existing));
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = paymentService.refund(existing.id());

        assertThat(response.description().status()).isEqualTo(TransactionStatus.CANCELADO);
    }

    @Test
    void shouldRejectRefundWhenTransactionIsAlreadyCancelled() {
        var canceled = PaymentTransactionFixture.build(
                "100023568900001", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 1
        ).withDescription(PaymentTransactionFixture.build(
                "100023568900001", BigDecimal.valueOf(500.50), PaymentMethodType.AVISTA, 1
        ).description().withStatus(TransactionStatus.CANCELADO));
        when(paymentRepository.findById(canceled.id())).thenReturn(Optional.of(canceled));

        assertThatThrownBy(() -> paymentService.refund(canceled.id()))
                .isInstanceOf(InvalidRefundException.class);
    }

    @Test
    void shouldThrowWhenRefundingUnknownTransaction() {
        when(paymentRepository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.refund("missing"))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    void shouldReturnAllTransactions() {
        var first = PaymentTransactionFixture.build("1", BigDecimal.TEN, PaymentMethodType.AVISTA, 1);
        var second = PaymentTransactionFixture.build("2", BigDecimal.ONE, PaymentMethodType.PARCELADO_LOJA, 2);
        when(paymentRepository.findAll()).thenReturn(List.of(first, second));

        var transactions = paymentService.getAll();

        assertThat(transactions).containsExactly(first, second);
    }
}
