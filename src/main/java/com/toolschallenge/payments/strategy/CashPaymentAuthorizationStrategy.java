package com.toolschallenge.payments.strategy;

import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.PaymentTransaction;
import com.toolschallenge.payments.domain.TransactionStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class CashPaymentAuthorizationStrategy implements PaymentAuthorizationStrategy {

    @Override
    public PaymentMethodType supportedType() {
        return PaymentMethodType.AVISTA;
    }

    @Override
    public TransactionStatus authorize(PaymentTransaction transaction) {
        boolean validAmount = transaction.description().amount().compareTo(BigDecimal.ZERO) > 0;
        boolean validInstallments = transaction.paymentMethod().installments() == 1;
        return validAmount && validInstallments ? TransactionStatus.AUTORIZADO : TransactionStatus.NEGADO;
    }
}
