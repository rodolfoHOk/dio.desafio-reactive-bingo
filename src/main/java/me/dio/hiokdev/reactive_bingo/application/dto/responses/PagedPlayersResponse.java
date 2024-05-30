package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import lombok.Builder;

import java.util.List;

public record PagedPlayersResponse(
        Long currentPage,
        Long totalPages,
        Long totalItens,
        List<PlayerResponse> content
) {

    @Builder(toBuilder = true)
    public PagedPlayersResponse {
    }

}
