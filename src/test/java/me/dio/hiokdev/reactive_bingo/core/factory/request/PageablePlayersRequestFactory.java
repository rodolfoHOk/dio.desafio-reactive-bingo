package me.dio.hiokdev.reactive_bingo.core.factory.request;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageablePlayersRequest;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.domain.enums.PlayerSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageablePlayersRequestFactory {

    public static PageablePlayersRequestFactoryBuilder builder() {
        return new PageablePlayersRequestFactoryBuilder();
    }

    public static class PageablePlayersRequestFactoryBuilder {

        private String sentence;
        private Long page;
        private Integer limit;
        private PlayerSortBy sortBy;
        private SortDirection sortDirection;
        private final Faker faker = FakerData.getFaker();

        public PageablePlayersRequestFactoryBuilder() {
            this.sentence = faker.lorem().word();
            this.page = faker.number().numberBetween(0L, 3L);
            this.limit = faker.number().numberBetween(20, 40);
            this.sortBy = FakerData.randomEnum(PlayerSortBy.class);
            this.sortDirection = FakerData.randomEnum(SortDirection.class);
        }

        public PageablePlayersRequestFactoryBuilder negativePage() {
            this.page = faker.number().numberBetween(Long.MIN_VALUE, 0);
            return this;
        }

        public PageablePlayersRequestFactoryBuilder lessThanZeroLimit() {
            this.limit = faker.number().numberBetween(Integer.MIN_VALUE, 1);
            return this;
        }

        public PageablePlayersRequestFactoryBuilder greaterThanFiftyLimit() {
            this.limit = faker.number().numberBetween(51, Integer.MAX_VALUE);
            return this;
        }

        public PageablePlayersRequest build() {
            return PageablePlayersRequest.builder()
                    .sentence(sentence)
                    .page(page)
                    .limit(limit)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();
        }

    }

}
