package me.dio.hiokdev.reactive_bingo.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortDirection {

    ASC("ASC"),
    DESC("DESC");

    private final String direction;

}
