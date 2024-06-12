package me.dio.hiokdev.reactive_bingo.core.factory.request;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageableRoundsRequest;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundsSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageableRoundsRequestFactory {

    public static PageableRoundsRequestFactoryBuilder builder() {
        return new PageableRoundsRequestFactoryBuilder();
    }

    public static class PageableRoundsRequestFactoryBuilder {

        private String sentence;
        private OffsetDateTime startDate;
        private OffsetDateTime endDate;
        private Long page;
        private Integer limit;
        private RoundsSortBy sortBy;
        private SortDirection sortDirection;
        private final Faker faker = FakerData.getFaker();

        public PageableRoundsRequestFactoryBuilder() {
            this.sentence = faker.lorem().word();
            this.startDate = (faker.date().past(365, TimeUnit.DAYS).toInstant()).atOffset(ZoneOffset.ofHours(-3));
            this.endDate = OffsetDateTime.now();
            this.page = faker.number().numberBetween(0L, 3L);
            this.limit = faker.number().numberBetween(20, 40);
            this.sortBy = FakerData.randomEnum(RoundsSortBy.class);
            this.sortDirection = FakerData.randomEnum(SortDirection.class);
        }

        public PageableRoundsRequestFactoryBuilder futureStartDate() {
            this.startDate = faker.date().future(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.ofHours(-3));
            return this;
        }

        public PageableRoundsRequestFactoryBuilder futureEndDate() {
            this.endDate = faker.date().future(1, TimeUnit.DAYS).toInstant().atOffset(ZoneOffset.ofHours(-3));
            return this;
        }

        public PageableRoundsRequestFactoryBuilder negativePage() {
            this.page = faker.number().numberBetween(Long.MIN_VALUE, 0);
            return this;
        }

        public PageableRoundsRequestFactoryBuilder lessThanZeroLimit() {
            this.limit = faker.number().numberBetween(Integer.MIN_VALUE, 1);
            return this;
        }

        public PageableRoundsRequestFactoryBuilder greaterThanFiftyLimit() {
            this.limit = faker.number().numberBetween(51, Integer.MAX_VALUE);
            return this;
        }

        public PageableRoundsRequest build() {
            return PageableRoundsRequest.builder()
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
