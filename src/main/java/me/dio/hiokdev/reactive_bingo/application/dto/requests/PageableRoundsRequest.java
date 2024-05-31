package me.dio.hiokdev.reactive_bingo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundsSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import org.apache.commons.lang3.ObjectUtils;

import java.time.OffsetDateTime;

public record PageableRoundsRequest(
        @JsonProperty("sentence")
        @Schema(description = "Texto para filtrar por status (case insensitive)", example = "INITIATED")
        String sentence,

        @JsonProperty("startDate")
        @PastOrPresent
        @Schema(description = "Data de", example = "2021-01-02T10:15:30-03:00", defaultValue = "-999999999-01-01T00:00:00+18:00")
        OffsetDateTime startDate,

        @JsonProperty("endDate")
        @PastOrPresent
        @Schema(description = "Data até", example = "2024-05-31T17:57:47-03:00", defaultValue = "Data e Hora do momento da requisição")
        OffsetDateTime endDate,

        @JsonProperty("page")
        @PositiveOrZero
        @Schema(description = "Pagina solicitada", example = "1", defaultValue = "1")
        Long page,

        @JsonProperty("limit")
        @Min(1)
        @Max(50)
        @Schema(description = "Tamanho da página", example = "30", defaultValue = "20")
        Integer limit,

        @JsonProperty("sortBy")
        @Schema(description = "Campo para ordenação", enumAsRef = true, defaultValue = "CREATE_DATE")
        RoundsSortBy sortBy,

        @JsonProperty("sortDirection")
        @Schema(description = "Sentido da ordenação", enumAsRef = true, defaultValue = "DESC")
        SortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public PageableRoundsRequest {
        sentence = ObjectUtils.defaultIfNull(sentence, "");
        startDate = ObjectUtils.defaultIfNull(startDate, OffsetDateTime.MIN);
        endDate = ObjectUtils.defaultIfNull(endDate, OffsetDateTime.now());
        page = ObjectUtils.defaultIfNull(page, 1L);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        sortBy = ObjectUtils.defaultIfNull(sortBy, RoundsSortBy.CREATE_DATE);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, SortDirection.DESC);
    }

}
