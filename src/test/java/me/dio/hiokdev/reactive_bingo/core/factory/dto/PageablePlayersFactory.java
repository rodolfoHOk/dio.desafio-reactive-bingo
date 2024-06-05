package me.dio.hiokdev.reactive_bingo.core.factory.dto;

import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageablePlayersRequest;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.enums.PlayerSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;

public class PageablePlayersFactory {

    public static PageablePlayersFactoryBuilder builder(final PageablePlayersRequest request) {
        return new PageablePlayersFactoryBuilder(request);
    }

    public static class PageablePlayersFactoryBuilder {

        private String sentence;
        private Long page;
        private Integer limit;
        private PlayerSortBy sortBy;
        private SortDirection sortDirection;

        public PageablePlayersFactoryBuilder(final PageablePlayersRequest request) {
            this.sentence = request.sentence();
            this.page = request.page();
            this.limit = request.limit();
            this.sortBy = request.sortBy();
            this.sortDirection = request.sortDirection();
        }

        public PageablePlayers build() {
            return PageablePlayers.builder()
                    .sentence(sentence)
                    .page(page)
                    .limit(limit)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
        }

    }

}
