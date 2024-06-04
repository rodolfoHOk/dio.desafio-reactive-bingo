package me.dio.hiokdev.reactive_bingo.core.factory.dto;

import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedPlayers;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;

import java.util.List;
import java.util.stream.Stream;

public class PagedPlayersFactory {

    public static PagedPlayersFactoryBuilder builder(final Integer pageSize) {
        return new PagedPlayersFactoryBuilder(pageSize);
    }

    public static class PagedPlayersFactoryBuilder {

        private Long currentPage;
        private Long totalPages;
        private Long totalItens;
        private List<Player> content;

        public PagedPlayersFactoryBuilder(final Integer pageSize) {
            var faker = FakerData.getFaker();
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.totalPages = faker.number().numberBetween(currentPage, 20);
            this.content = Stream.generate(() -> PlayerFactory.builder().build())
                    .limit(pageSize)
                    .toList();
            this.totalItens = faker.number().numberBetween(pageSize * (totalPages - 1), pageSize * totalPages);
        }

        public PagedPlayersFactoryBuilder emptyPage() {
            this.currentPage = 1L;
            this.totalPages = 0L;
            this.totalItens = 0L;
            this.content = List.of();
            return this;
        }

        public PagedPlayers build() {
            return PagedPlayers.builder()
                    .currentPage(this.currentPage)
                    .totalPages(this.totalPages)
                    .totalItens(this.totalItens)
                    .content(this.content)
                    .build();
        }

    }

}
