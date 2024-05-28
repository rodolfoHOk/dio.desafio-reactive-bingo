package me.dio.hiokdev.reactive_bingo.domain.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

public record BingoCard(
        String id,
        Player player,
        List<Integer> numbers,
        Integer hintCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static BingoCardBuilder builder() {
        return new BingoCardBuilder();
    }

    public BingoCardBuilder toBuilder() {
        return new BingoCardBuilder(id, player, numbers, hintCount, createdAt, updatedAt);
    }

    public Mono<Boolean> isCompleted() {
        return Mono.just(hintCount == 20);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class BingoCardBuilder {

        private String id;
        private Player player;
        private List<Integer> numbers = List.of();
        private Integer hintCount;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public BingoCardBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public BingoCardBuilder player(final Player player) {
            this.player = player;
            return this;
        }

        public BingoCardBuilder numbers(final List<Integer> numbers) {
            this.numbers = numbers;
            return this;
        }

        public BingoCardBuilder hintCount(final Integer hintCount) {
            this.hintCount = hintCount;
            return this;
        }

        public BingoCardBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BingoCardBuilder updatedAt(final OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Mono<BingoCardBuilder> generate(final Player player, final List<BingoCard> existingBingoCards) {
            return generateNumbers(existingBingoCards, new Random())
                    .map(numbers -> this.id(ObjectId.get().toString())
                            .player(player)
                            .numbers(numbers)
                            .hintCount(0)
                            .createdAt(OffsetDateTime.now())
                            .updatedAt(OffsetDateTime.now()));
        }

        public Mono<BingoCardBuilder> incrementHintCountIfContains(final Integer sortedNumber) {
            return Mono.defer(() -> {
                if (numbers.contains(sortedNumber)) {
                    ++this.hintCount;
                    this.updatedAt = OffsetDateTime.now();
                }
                return Mono.just(this);
            });
        }

        public BingoCard build() {
            return new BingoCard(id, player, numbers, hintCount, createdAt, updatedAt);
        }

        private Mono<List<Integer>> generateNumbers(final List<BingoCard> existingBingoCards, final Random random) {
            return Flux.generate((SynchronousSink<Integer> sink) -> sink.next(random.nextInt(100)))
                    .distinct()
                    .take(20)
                    .collectSortedList()
                    .flatMap(sortedNumbers -> numbersNotAreValid(sortedNumbers, existingBingoCards)
                            .flatMap(notAreValid -> notAreValid
                                    ? generateNumbers(existingBingoCards, random)
                                    : Mono.just(sortedNumbers)));
        }

        private Mono<Boolean> numbersNotAreValid(final List<Integer> sortedNumbers, final List<BingoCard> bingoCards) {
            return Flux.fromIterable(bingoCards)
                    .flatMap(bingoCard -> countDuplicates(sortedNumbers, bingoCard.numbers))
                    .any(duplicates -> duplicates > 5);
        }

        private Mono<Long> countDuplicates(
                final List<Integer> sortedNumbers,
                final List<Integer> existingBingoCardNumbers
        ) {
            return Flux.fromIterable(sortedNumbers)
                    .filter(existingBingoCardNumbers::contains)
                    .count();
        }

    }

}
