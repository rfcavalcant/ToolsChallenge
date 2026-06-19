package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record DescriptionRequestDto(
        @JsonProperty("valor")
        @NotNull
        BigDecimal amount,

        @JsonProperty("dataHora")
        @NotBlank
        @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$", message = "dataHora deve seguir o formato dd/MM/yyyy HH:mm:ss")
        String dateTime,

        @JsonProperty("estabelecimento")
        @NotBlank
        String establishment
) {
}
