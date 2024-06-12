package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record SortedNumberResponse(
        @JsonProperty("sortedNumber")
        @Schema(description = "Número sorteado", example = "47")
        Integer sortedNumber
) {

    @Builder
    public SortedNumberResponse {
    }

}
