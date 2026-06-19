package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(
        @JsonProperty("transacao")
        @NotNull
        @Valid
        TransactionRequestDto transaction
) {
}
