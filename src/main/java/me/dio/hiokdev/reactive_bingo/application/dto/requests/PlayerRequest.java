package me.dio.hiokdev.reactive_bingo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record PlayerRequest(
        @JsonProperty("name")
        @NotBlank
        @Size(min = 3, max = 150)
        String name,

        @JsonProperty("email")
        @Email
        @NotBlank
        @Size(min = 3, max = 150)
        String email
) {

    @Builder(toBuilder = true)
    public PlayerRequest {
    }

}
