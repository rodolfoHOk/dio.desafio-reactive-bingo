package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record FieldErrorResponse(
        @JsonProperty("name")
        @Schema(description = "Nome do campo com erro", example = "name")
        String name,

        @JsonProperty("message")
        @Schema(description = "Descrição do erro", example = "o nome deve ter no máximo 150 caracteres")
        String message
) {

    @Builder
    public FieldErrorResponse {
    }

}
