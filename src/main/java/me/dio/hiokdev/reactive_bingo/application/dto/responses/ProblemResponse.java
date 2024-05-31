package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

public record ProblemResponse(
        @JsonProperty("status")
        Integer status,

        @JsonProperty("description")
        String description,

        @JsonProperty("timestamp")
        OffsetDateTime timestamp,

        @JsonProperty("fields")
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
