package com.toolschallenge.payments.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponseDto(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> details
) {
}
