package me.dio.hiokdev.reactive_bingo.application.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

public record PagedRoundsResponse(
        @JsonProperty("currentPage")
        @Schema(description = "Pagina retornada", example = "1")
        Long currentPage,

        @JsonProperty("totalPages")
        @Schema(description = "Total de páginas", example = "20")
        Long totalPages,

        @JsonProperty("totalItens")
        @Schema(description = "Quantidade de registros paginados", example = "100")
        Long totalItens,

        @JsonProperty("content")
        @Schema(description = "Dados das rodadas da página")
        List<RoundResponse> content
) {

    @Builder
    public PagedRoundsResponse {
    }

}
