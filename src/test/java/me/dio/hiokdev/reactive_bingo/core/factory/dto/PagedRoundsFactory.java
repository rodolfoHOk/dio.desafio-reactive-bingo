package me.dio.hiokdev.reactive_bingo.core.factory.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedRounds;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;

import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PagedRoundsFactory {

    public static PagedRoundsFactoryBuilder builder(final Integer pageSize) {
        return new PagedRoundsFactoryBuilder(pageSize);
    }

    public static class PagedRoundsFactoryBuilder {

        private Long currentPage;
        private Long totalPages;
        private Long totalItens;
        private List<Round> content;

        public PagedRoundsFactoryBuilder(final Integer pageSize) {
            var faker = FakerData.getFaker();
            this.currentPage = faker.number().numberBetween(1L, 20L);
            this.totalPages = faker.number().numberBetween(currentPage, 20);
            this.content = Stream.generate(() -> RoundFactory.builder().build())
                    .limit(pageSize)
                    .toList();
            this.totalItens = faker.number().numberBetween(pageSize * (totalPages - 1), pageSize * totalPages);
        }

        public PagedRoundsFactoryBuilder emptyPage() {
            this.currentPage = 1L;
            this.totalPages = 0L;
            this.totalItens = 0L;
            this.content = List.of();
            return this;
        }

        public PagedRounds build() {
            return PagedRounds.builder()
                    .currentPage(this.currentPage)
                    .totalPages(this.totalPages)
                    .totalItens(this.totalItens)
                    .content(this.content)
                    .build();
        }

    }

}
