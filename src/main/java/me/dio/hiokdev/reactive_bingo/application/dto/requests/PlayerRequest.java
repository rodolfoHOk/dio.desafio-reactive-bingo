package me.dio.hiokdev.reactive_bingo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record PlayerRequest(
        @JsonProperty("name")
        @NotBlank
        @Size(min = 3, max = 150)
        @Schema(description = "Nome do jogador", example = "João Jogador")
        String name,

        @JsonProperty("email")
        @Email
        @NotBlank
        @Size(min = 3, max = 150)
        @Schema(description = "E-mail do jogador", example = "joao@gamer.com.br")
        String email
) {

    @Builder(toBuilder = true)
    public PlayerRequest {
    }

}
