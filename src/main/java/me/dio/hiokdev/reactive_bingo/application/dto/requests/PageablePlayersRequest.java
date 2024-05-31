package me.dio.hiokdev.reactive_bingo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.enums.PlayerSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import org.apache.commons.lang3.ObjectUtils;

public record PageablePlayersRequest(
        @JsonProperty("sentence")
        @Schema(description = "Texto para filtrar por nome ou e-mail (case insensitive)", example = "ana")
        String sentence,

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
        @Schema(description = "Campo para ordenação", enumAsRef = true, defaultValue = "NAME")
        PlayerSortBy sortBy,

        @JsonProperty("sortDirection")
        @Schema(description = "Sentido da ordenação", enumAsRef = true, defaultValue = "ASC")
        SortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public PageablePlayersRequest {
        sentence = ObjectUtils.defaultIfNull(sentence, "");
        page = ObjectUtils.defaultIfNull(page, 1L);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        sortBy = ObjectUtils.defaultIfNull(sortBy, PlayerSortBy.NAME);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, SortDirection.ASC);
    }

}
