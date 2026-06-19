package com.toolschallenge.payments.strategy;

import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.PaymentTransaction;
import com.toolschallenge.payments.domain.TransactionStatus;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class StoreInstallmentAuthorizationStrategy implements PaymentAuthorizationStrategy {

    @Override
    public PaymentMethodType supportedType() {
        return PaymentMethodType.PARCELADO_LOJA;
    }

    @Override
    public TransactionStatus authorize(PaymentTransaction transaction) {
        boolean validAmount = transaction.description().amount().compareTo(BigDecimal.ZERO) > 0;
        int installments = transaction.paymentMethod().installments();
        boolean validInstallments = installments >= 2 && installments <= 12;
        return validAmount && validInstallments ? TransactionStatus.AUTORIZADO : TransactionStatus.NEGADO;
    }
}
