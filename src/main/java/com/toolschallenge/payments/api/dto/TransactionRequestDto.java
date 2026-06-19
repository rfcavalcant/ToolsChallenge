package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TransactionRequestDto(
        @JsonProperty("cartao")
        @NotBlank
        @Pattern(regexp = "^\\d{4}\\*{8}\\d{4}$", message = "cartao deve seguir o formato 1234********1234")
        String card,

        @JsonProperty("id")
        @NotBlank
        @Size(max = 50)
        String id,

        @JsonProperty("descricao")
        @NotNull
        @Valid
        DescriptionRequestDto description,

        @JsonProperty("formaPagamento")
        @NotNull
        @Valid
        PaymentMethodRequestDto paymentMethod
) {
}
