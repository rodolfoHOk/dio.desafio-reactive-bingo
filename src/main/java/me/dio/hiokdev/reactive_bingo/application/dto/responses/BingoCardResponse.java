package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public record BingoCardResponse(
        @JsonProperty("playerId")
        @Schema(description = "Identificador do jogador", example = "66342b8418c87a1a8a8ffcb0")
        String playerId,

        @JsonProperty("numbers")
        @Schema(description = "Números do cartão", example = "[1, 23, 78, 99]")
        List<Integer> numbers,

        @JsonProperty("hintCount")
        @Schema(description = "Contagem de acertos", example = "15")
        Integer hintCount
) {

    @Builder
    public BingoCardResponse {
    }

}
