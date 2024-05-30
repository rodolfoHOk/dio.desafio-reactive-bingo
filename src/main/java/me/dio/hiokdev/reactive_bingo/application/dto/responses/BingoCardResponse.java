package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

public record BingoCardResponse(
        @JsonProperty("playerId")
        String playerId,

        @JsonProperty("numbers")
        List<Integer> numbers,

        @JsonProperty("hintCount")
        Integer hintCount
) {

    @Builder(toBuilder = true)
    public BingoCardResponse {
    }

}
