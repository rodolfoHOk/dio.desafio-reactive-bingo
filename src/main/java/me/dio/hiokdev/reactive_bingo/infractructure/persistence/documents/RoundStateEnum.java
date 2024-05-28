package me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoundStateEnum {

    CREATED("created"),
    INITIATED("initiated"),
    FINISHED("finished");

    private final String description;

}
