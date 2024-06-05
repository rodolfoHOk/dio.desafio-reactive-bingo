package me.dio.hiokdev.reactive_bingo.core.factory.domain;

import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundState;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoundFactory {

    public static RoundFactoryBuilder builder() {
        return new RoundFactoryBuilder();
    }

    public static class RoundFactoryBuilder {

        private String id;
        private List<BingoCard> bingoCards;
        private List<Integer> sortedNumbers;
        private List<String> winnersIds;
        private RoundState state;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private final Faker faker = FakerData.getFaker();

        public RoundFactoryBuilder() {
            this.id = ObjectId.get().toString();
            this.bingoCards = new ArrayList<>();
            this.sortedNumbers = new ArrayList<>();
            this.winnersIds = new ArrayList<>();
            this.state = RoundState.CREATED;
            this.createdAt = faker.date().between(faker.date().past(5, TimeUnit.DAYS), new Date())
                    .toInstant().atOffset(ZoneOffset.ofHours(-3));
            this.updatedAt = faker.date().between(Date.from(createdAt.toInstant()), new Date())
                    .toInstant().atOffset(ZoneOffset.ofHours(-3));
        }

        public RoundFactoryBuilder randomState() {
            this.state = FakerData.randomEnum(RoundState.class);
            return this;
        }

        public RoundFactoryBuilder preInsert() {
            this.id = null;
            this.createdAt = null;
            this.updatedAt = null;
            return this;
        }

        public Round build() {
            return Round.builder()
                    .id(id)
                    .bingoCards(bingoCards)
                    .sortedNumbers(sortedNumbers)
                    .winnersIds(winnersIds)
                    .state(state)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

    }

}
