package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentResponseDto(
        @JsonProperty("transacao")
        TransactionResponseDto transaction
) {
}
