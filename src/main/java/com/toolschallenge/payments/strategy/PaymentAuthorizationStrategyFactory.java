package com.toolschallenge.payments.strategy;

import com.toolschallenge.payments.domain.PaymentMethodType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PaymentAuthorizationStrategyFactory {

    private final Map<PaymentMethodType, PaymentAuthorizationStrategy> strategies;

    public PaymentAuthorizationStrategyFactory(List<PaymentAuthorizationStrategy> strategyList) {
        this.strategies = new EnumMap<>(PaymentMethodType.class);
        strategyList.forEach(strategy -> strategies.put(strategy.supportedType(), strategy));
    }

    public PaymentAuthorizationStrategy getStrategy(PaymentMethodType type) {
        PaymentAuthorizationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Tipo de pagamento nao suportado: " + type);
        }
        return strategy;
    }
}
