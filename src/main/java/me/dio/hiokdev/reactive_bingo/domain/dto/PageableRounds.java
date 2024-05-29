package me.dio.hiokdev.reactive_bingo.domain.dto;

import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundsSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import org.apache.commons.lang3.ObjectUtils;

import java.time.OffsetDateTime;

public record PageableRounds(
        String sentence,
        OffsetDateTime startDate,
        OffsetDateTime endDate,
        Long page,
        Integer limit,
        RoundsSortBy sortBy,
        SortDirection sortDirection
) {

    @Builder(toBuilder = true)
    public PageableRounds {
        sentence = ObjectUtils.defaultIfNull(sentence, "");
        startDate = ObjectUtils.defaultIfNull(startDate, OffsetDateTime.MIN);
        endDate = ObjectUtils.defaultIfNull(endDate, OffsetDateTime.now());
        page = ObjectUtils.defaultIfNull(page, 1L);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        sortBy = ObjectUtils.defaultIfNull(sortBy, RoundsSortBy.CREATE_DATE);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, SortDirection.DESC);
    }

    public Long getSkip() {
        return page > 1 ? (page - 1) * limit : 0;
    }

}
