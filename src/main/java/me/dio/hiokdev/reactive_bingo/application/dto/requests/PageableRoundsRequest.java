package me.dio.hiokdev.reactive_bingo.application.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        String sentence,

        @JsonProperty("sentence")
        @PastOrPresent
        OffsetDateTime startDate,

        @JsonProperty("sentence")
        @PastOrPresent
        OffsetDateTime endDate,

        @JsonProperty("page")
        @PositiveOrZero
        Long page,

        @JsonProperty("limit")
        @Min(1)
        @Max(50)
        Integer limit,

        @JsonProperty("sortBy")
        RoundsSortBy sortBy,

        @JsonProperty("sortDirection")
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
