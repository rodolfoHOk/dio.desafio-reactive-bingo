package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.List;

public record PagedRoundsResponse(
        @JsonProperty("currentPage")
        Long currentPage,

        @JsonProperty("totalPages")
        Long totalPages,

        @JsonProperty("totalItens")
        Long totalItens,

        @JsonProperty("content")
        List<RoundResponse> content
) {

    @Builder(toBuilder = true)
    public PagedRoundsResponse {
    }

}
