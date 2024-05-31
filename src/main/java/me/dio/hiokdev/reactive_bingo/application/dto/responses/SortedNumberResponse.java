package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import lombok.Builder;

public record SortedNumberResponse(
        Integer sortedNumber
) {

    @Builder(toBuilder = true)
    public SortedNumberResponse {
    }

}
