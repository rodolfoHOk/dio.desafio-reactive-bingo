package me.dio.hiokdev.reactive_bingo.domain.models;

import lombok.Builder;

import java.time.OffsetDateTime;

public record Player(
        String id,
        String name,
        String email,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    @Builder(toBuilder = true)
    public Player {}

}
