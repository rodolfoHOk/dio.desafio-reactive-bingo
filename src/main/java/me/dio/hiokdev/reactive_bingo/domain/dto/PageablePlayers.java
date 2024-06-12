package me.dio.hiokdev.reactive_bingo.domain.dto;

import lombok.Builder;
import me.dio.hiokdev.reactive_bingo.domain.enums.PlayerSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import org.apache.commons.lang3.ObjectUtils;

public record PageablePlayers(
        String sentence,
        Long page,
        Integer limit,
        PlayerSortBy sortBy,
        SortDirection sortDirection
) {

    @Builder
    public PageablePlayers {
        sentence = ObjectUtils.defaultIfNull(sentence, "");
        page = ObjectUtils.defaultIfNull(page, 1L);
        limit = ObjectUtils.defaultIfNull(limit, 20);
        sortBy = ObjectUtils.defaultIfNull(sortBy, PlayerSortBy.NAME);
        sortDirection = ObjectUtils.defaultIfNull(sortDirection, SortDirection.ASC);
    }

    public Long getSkip() {
        return page > 1 ? (page - 1) * limit : 0;
    }

}
