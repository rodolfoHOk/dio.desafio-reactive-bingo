package me.dio.hiokdev.reactive_bingo.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoundState {

    CREATED("created"),
    FINISHED("finished"),
    INITIATED("initiated");

    private final String description;

}
