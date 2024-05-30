package me.dio.hiokdev.reactive_bingo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.enums.PlayerSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import org.apache.commons.lang3.ObjectUtils;

public record PageablePlayersRequest(
        @JsonProperty("sentence")
        String sentence,

        @JsonProperty("page")
        @PositiveOrZero
        Long page,

        @JsonProperty("limit")
        @Min(1)
        @Max(50)
        Integer limit,

        @JsonProperty("sortBy")
        PlayerSortBy sortBy,

        @JsonProperty("sortDirection")
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
