package com.toolschallenge.payments.api.mapper;

import com.toolschallenge.payments.api.dto.DescriptionResponseDto;
import com.toolschallenge.payments.api.dto.PaymentMethodResponseDto;
import com.toolschallenge.payments.api.dto.PaymentRequestDto;
import com.toolschallenge.payments.api.dto.PaymentResponseDto;
import com.toolschallenge.payments.api.dto.TransactionResponseDto;
import com.toolschallenge.payments.domain.PaymentDescription;
import com.toolschallenge.payments.domain.PaymentMethod;
import com.toolschallenge.payments.domain.PaymentMethodType;
import com.toolschallenge.payments.domain.PaymentTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public PaymentTransaction toDomain(PaymentRequestDto dto) {
        return new PaymentTransaction(
                dto.transaction().card(),
                dto.transaction().id(),
                new PaymentDescription(
                        new BigDecimal(dto.transaction().description().amount()),
                        LocalDateTime.parse(dto.transaction().description().dateTime(), DATE_TIME_FORMATTER),
                        dto.transaction().description().establishment(),
                        null,
                        null,
                        null
                ),
                new PaymentMethod(
                        toPaymentMethodType(dto.transaction().paymentMethod().type()),
                        Integer.parseInt(dto.transaction().paymentMethod().installments())
                )
        );
    }

    public PaymentResponseDto toResponse(PaymentTransaction transaction) {
        return new PaymentResponseDto(new TransactionResponseDto(
                transaction.card(),
                transaction.id(),
                new DescriptionResponseDto(
                        transaction.description().amount().toPlainString(),
                        transaction.description().dateTime().format(DATE_TIME_FORMATTER),
                        transaction.description().establishment(),
                        transaction.description().nsu(),
                        transaction.description().authorizationCode(),
                        transaction.description().status().name()
                ),
                new PaymentMethodResponseDto(
                        toJsonPaymentMethodType(transaction.paymentMethod().type()),
                        String.valueOf(transaction.paymentMethod().installments())
                )
        ));
    }

    private PaymentMethodType toPaymentMethodType(String type) {
        return switch (type.toUpperCase(Locale.ROOT)) {
            case "AVISTA" -> PaymentMethodType.AVISTA;
            case "PARCELADO LOJA" -> PaymentMethodType.PARCELADO_LOJA;
            case "PARCELADO EMISSOR" -> PaymentMethodType.PARCELADO_EMISSOR;
            default -> throw new IllegalArgumentException("Tipo de pagamento invalido: " + type);
        };
    }

    private String toJsonPaymentMethodType(PaymentMethodType type) {
        return switch (type) {
            case AVISTA -> "AVISTA";
            case PARCELADO_LOJA -> "PARCELADO LOJA";
            case PARCELADO_EMISSOR -> "PARCELADO EMISSOR";
        };
    }
}
