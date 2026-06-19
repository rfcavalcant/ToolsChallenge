package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record DescriptionRequestDto(
        @JsonProperty("valor")
        @NotBlank
        @Pattern(regexp = "^\\d+(\\.\\d{1,2})?$", message = "valor deve seguir o formato 500.50")
        @JsonDeserialize(using = NumericStringDeserializer.class)
        String amount,

        @JsonProperty("dataHora")
        @NotBlank
        @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$", message = "dataHora deve seguir o formato dd/MM/yyyy HH:mm:ss")
        String dateTime,

        @JsonProperty("estabelecimento")
        @NotBlank
        String establishment
) {
}
