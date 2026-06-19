package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionResponseDto(
        @JsonProperty("cartao")
        String card,

        @JsonProperty("id")
        String id,

        @JsonProperty("descricao")
        DescriptionResponseDto description,

        @JsonProperty("formaPagamento")
        PaymentMethodResponseDto paymentMethod
) {
}
