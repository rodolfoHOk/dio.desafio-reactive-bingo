package me.dio.hiokdev.reactive_bingo.domain.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundState;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyFinishedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundNotInitiatedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Random;

public record Round(
        String id,
        List<BingoCard> bingoCards,
        List<Integer> sortedNumbers,
        RoundState state,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static RoundBuilder builder() {
        return new RoundBuilder();
    }

    public RoundBuilder toBuilder() {
        return new RoundBuilder(id, bingoCards, sortedNumbers, state, createdAt, updatedAt);
    }

    public Mono<Integer> getLastSortedNumber() {
        return Flux.fromIterable(this.sortedNumbers).last();
    }

    public Flux<BingoCard> getWinners() {
        return Flux.fromIterable(this.bingoCards)
                .flatMap(bingoCard -> bingoCard.isCompleted()
                        .filter(Boolean.TRUE::equals)
                        .map(completed -> bingoCard));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundBuilder {

        private String id;
        private List<BingoCard> bingoCards = List.of();
        private List<Integer> sortedNumbers = List.of();
        private RoundState state;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public RoundBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public RoundBuilder bingoCards(final List<BingoCard> bingoCards) {
            this.bingoCards = bingoCards;
            return this;
        }

        public RoundBuilder bingoCard(final BingoCard bingoCards) {
            this.bingoCards.add(bingoCards);
            return this;
        }

        public RoundBuilder sortedNumbers(final List<Integer> sortedNumbers) {
            this.sortedNumbers = sortedNumbers;
            return this;
        }

        public RoundBuilder sortedNumber(final Integer sortedNumber) {
            this.sortedNumbers.add(sortedNumber);
            return this;
        }

        public RoundBuilder state(final RoundState state) {
            this.state = state;
            return this;
        }

        public RoundBuilder createdAt(final OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public RoundBuilder updatedAt(final OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Mono<RoundBuilder> addBingoCard(final Player player) {
            return Mono.defer(() -> {
                if (state != RoundState.CREATED) {
                    return Mono.error(new RoundAlreadyInitiatedException("Rodada já foi iniciada"));
                }
                return BingoCard.builder().generate(player, this.bingoCards)
                        .map(BingoCard.BingoCardBuilder::build)
                        .map(bingoCard -> this.bingoCard(bingoCard).updatedAt(OffsetDateTime.now()));
            });
        }

        public Mono<RoundBuilder> sortNumber() {
            return Mono.defer(() -> {
                if (state == RoundState.FINISHED) {
                    return Mono.error(new RoundAlreadyFinishedException("Rodada já foi terminada"));
                }
                if (state == RoundState.CREATED) {
                    this.state = RoundState.INITIATED;
                }
                var random = new Random();
                return sortUniqueNumber(random)
                        .flatMap(this::processSortedNumber);
            });
        }

        public Mono<RoundBuilder> finish() {
            return Mono.defer(() -> {
                if (state == RoundState.FINISHED) {
                    return Mono.error(new RoundAlreadyFinishedException("Rodada já foi terminada"));
                }
                if (state == RoundState.INITIATED) {
                    return Mono.error(new RoundNotInitiatedException("Rodada ainda não foi iniciada"));
                }
                return Mono.just(this.state(RoundState.FINISHED).updatedAt(OffsetDateTime.now()));
            });
        }

        public Round build() {
            return new Round(id, bingoCards, sortedNumbers, state, createdAt, updatedAt);
        }

        private Mono<Integer> sortUniqueNumber(final Random random) {
            return Mono.create(sink -> {
                int sortedNumber;
                do {
                    sortedNumber = random.nextInt(100);
                } while (sortedNumbers.contains(sortedNumber));
                sink.success(sortedNumber);
            });
        }

        private Mono<RoundBuilder> processSortedNumber(final Integer sortedNumber) {
            return Mono.just(this.sortedNumber(sortedNumber).updatedAt(OffsetDateTime.now()))
                    .flatMap(roundBuilder -> updateBingoCards(sortedNumber));
        }

        private Mono<RoundBuilder> updateBingoCards(final Integer sortedNumber) {
            return Flux.fromIterable(this.bingoCards)
                    .flatMap(bingoCard -> bingoCard.toBuilder().incrementHintCountIfContains(sortedNumber))
                    .map(BingoCard.BingoCardBuilder::build)
                    .collectList()
                    .map(updatedCards -> {
                        this.bingoCards = updatedCards;
                        return this;
                    });
        }

    }

}
