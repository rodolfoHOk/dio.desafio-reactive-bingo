package me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoundStateEnum {

    CREATED("created"),
    FINISHED("finished"),
    INITIATED("initiated");

    private final String description;

}
