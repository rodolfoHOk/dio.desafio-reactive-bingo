package me.dio.hiokdev.reactive_bingo.core.factory.dto;

import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageableRoundsRequest;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundsSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;

import java.time.OffsetDateTime;

public class PageableRoundsFactory {

    public static PageableRoundsFactoryBuilder builder(final PageableRoundsRequest request) {
        return new PageableRoundsFactoryBuilder(request);
    }

    public static class PageableRoundsFactoryBuilder {

        private String sentence;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private Long page;
        private Integer limit;
        private RoundsSortBy sortBy;
        private SortDirection sortDirection;

        public PageableRoundsFactoryBuilder(final PageableRoundsRequest request) {
            this.sentence = request.sentence();
            this.startDate = request.startDate();
            this.endDate = request.endDate();
            this.page = request.page();
            this.limit = request.limit();
            this.sortBy = request.sortBy();
            this.sortDirection = request.sortDirection();
        }

        public PageableRounds build() {
            return PageableRounds.builder()
                    .sentence(sentence)
                    .startDate(startDate)
                    .endDate(endDate)
                    .page(page)
                    .limit(limit)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
        }

    }

}
