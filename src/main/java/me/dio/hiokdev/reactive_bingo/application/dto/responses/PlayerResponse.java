package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

public record PlayerResponse(
        @JsonProperty("id")
        @Schema(description = "Identificador do jogador", example = "66342b8418c87a1a8a8ffcb0")
        String id,

        @JsonProperty("name")
        @Schema(description = "Nome do jogador", example = "João Jogador")
        String name,

        @JsonProperty("email")
        @Schema(description = "E-mail do jogador", example = "joao@gamer.com.br")
        String email
) {

    @Builder
    public PlayerResponse {
    }

}
