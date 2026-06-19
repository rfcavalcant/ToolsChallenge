package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PaymentMethodRequestDto(
        @JsonProperty("tipo")
        @NotBlank
        @Pattern(
                regexp = "^(AVISTA|PARCELADO LOJA|PARCELADO EMISSOR)$",
                message = "tipo deve ser AVISTA, PARCELADO LOJA ou PARCELADO EMISSOR"
        )
        String type,

        @JsonProperty("parcelas")
        @NotBlank
        @Pattern(regexp = "^\\d+$", message = "parcelas deve conter apenas numeros")
        String installments
) {
}
