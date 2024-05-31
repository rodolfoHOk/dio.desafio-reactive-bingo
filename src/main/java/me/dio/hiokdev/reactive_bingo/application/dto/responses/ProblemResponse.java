package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

public record ProblemResponse(
        @JsonProperty("status")
        @Schema(description = "Http status retornado", example = "400")
        Integer status,

        @JsonProperty("description")
        @Schema(description = "Descrição do erro", example = "Sua requisição tem informações inválidas")
        String description,

        @JsonProperty("timestamp")
        @Schema(description = "Momento em que o erro aconteceu", format = "datetime", example = "2021-01-02T10:15:30-03:00")
        OffsetDateTime timestamp,

        @JsonProperty("fields")
        @Schema(description = "Caso a requisição tenha parâmetros inválidos aqui serão informados os erros referentes aos mesmos")
        List<FieldErrorResponse> fields
) {

    public static ProblemResponseBuilder builder() {
        return new ProblemResponseBuilder();
    }

    public ProblemResponseBuilder toBuilder() {
        return new ProblemResponseBuilder(status, description, timestamp, fields);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProblemResponseBuilder {

        Integer status;
        String description;
        OffsetDateTime timestamp;
        List<FieldErrorResponse> fields;

        public ProblemResponseBuilder status(final Integer status) {
            this.status = status;
            return this;
        }

        public ProblemResponseBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public ProblemResponseBuilder timestamp(final OffsetDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ProblemResponseBuilder fields(final List<FieldErrorResponse> fields) {
            this.fields = fields;
            return this;
        }

        public ProblemResponseBuilder create(final Integer status, final String description) {
            return new ProblemResponseBuilder()
                    .status(status)
                    .description(description)
                    .timestamp(OffsetDateTime.now());
        }

        public ProblemResponse build() {
            return new ProblemResponse(status, description, timestamp, fields);
        }

    }

}
