package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record FieldErrorResponse(
        @JsonProperty("name")
        String name,

        @JsonProperty("message")
        String message
) {

    @Builder(toBuilder = true)
    public FieldErrorResponse {
    }

}
