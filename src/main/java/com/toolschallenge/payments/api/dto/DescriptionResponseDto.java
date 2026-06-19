package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DescriptionResponseDto(
        @JsonProperty("valor")
        String amount,

        @JsonProperty("dataHora")
        String dateTime,

        @JsonProperty("estabelecimento")
        String establishment,

        @JsonProperty("nsu")
        String nsu,

        @JsonProperty("codigoAutorizacao")
        String authorizationCode,

        @JsonProperty("status")
        String status
) {
}
