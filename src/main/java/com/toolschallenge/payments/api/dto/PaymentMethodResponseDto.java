package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentMethodResponseDto(
        @JsonProperty("tipo")
        String type,

        @JsonProperty("parcelas")
        String installments
) {
}
