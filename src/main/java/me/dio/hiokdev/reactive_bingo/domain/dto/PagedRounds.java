package me.dio.hiokdev.reactive_bingo.domain.dto;

import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;

import java.util.List;

public record PagedRounds(
        Long currentPage,
        Long totalPages,
        Long totalItens,
        List<Round> content
) {

    @Builder
    public PagedRounds {
    }

}
