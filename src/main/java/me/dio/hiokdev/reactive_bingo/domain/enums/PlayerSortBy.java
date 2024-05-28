package me.dio.hiokdev.reactive_bingo.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlayerSortBy {

    NAME("name"),
    EMAIL("email");

    private final String field;

}
