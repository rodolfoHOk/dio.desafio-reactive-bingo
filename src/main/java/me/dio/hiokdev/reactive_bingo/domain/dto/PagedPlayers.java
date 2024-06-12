package me.dio.hiokdev.reactive_bingo.domain.dto;

import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;

import java.util.List;

public record PagedPlayers(
        Long currentPage,
        Long totalPages,
        Long totalItens,
        List<Player> content
) {

    @Builder
    public PagedPlayers {
    }

}
