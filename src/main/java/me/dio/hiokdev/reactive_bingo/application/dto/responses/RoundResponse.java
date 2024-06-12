package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

public record RoundResponse(
        @JsonProperty("id")
        @Schema(description = "Identificador da rodada", example = "66351f41c475b40f15b62591")
        String id,

        @JsonProperty("bingoCards")
        @Schema(description = "Cartões de bingos da rodada")
        List<BingoCardResponse> bingoCards,

        @JsonProperty("sortedNumbers")
        @Schema(description = "Números sorteados na rodada", example = "[12, 34, 56, 78, 90]")
        List<Integer> sortedNumbers,

        @JsonProperty("winnersIds")
        @Schema(description = "Identificadores dos jogadores que ganharam a rodada", example = "[\"66342b8418c87a1a8a8ffcb0\", \"66351f41c475b40f15b62591\"]")
        List<String> winnersIds,

        @JsonProperty("state")
        @Schema(description = "Estado atual da rodada", example = "FINISHED")
        String state,

        @JsonProperty("createdAt")
        @Schema(description = "Data de criação da rodada", example = "2024-05-31T17:57:47-03:00")
        OffsetDateTime createdAt
) {

    @Builder
    public RoundResponse {
    }

}
