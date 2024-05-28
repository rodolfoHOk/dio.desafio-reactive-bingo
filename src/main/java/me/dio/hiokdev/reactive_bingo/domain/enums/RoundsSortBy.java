package me.dio.hiokdev.reactive_bingo.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoundsSortBy {

    STATE("state"),
    CREATE_DATE("created_at"),
    UPDATE_DATE("updated_at");

    private final String field;

}
