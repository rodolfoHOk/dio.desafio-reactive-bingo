package me.dio.hiokdev.reactive_bingo.core.factory.domain;

import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;

public record RoundToFinish(
        Round round,
        Integer sortNumberToSort
) {

    @Builder(toBuilder = true)
    public RoundToFinish {
    }

}
