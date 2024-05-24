package me.dio.hiokdev.reactive_bingo.domain.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundState;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyFinishedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundNotInitiatedException;

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

    public List<BingoCard> getWinners() {
        return bingoCards.stream().filter(BingoCard::isCompleted).toList();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoundBuilder {

        private String id;
        private List<BingoCard> bingoCards;
        private List<Integer> sortedNumbers;
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

        public RoundBuilder sortedNumbers(final List<Integer> sortedNumbers) {
            this.sortedNumbers = sortedNumbers;
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

        public RoundBuilder addBingoCard(final Player player) {
            if (state != RoundState.CREATED) throw new RoundAlreadyInitiatedException("Rodada já foi iniciada");
            BingoCard bingoCard;
            do {
                bingoCard = BingoCard.builder().generate(player).build();
            } while (!bingoCard.checkIsValid(bingoCards));
            this.bingoCards.add(bingoCard);
            this.updatedAt = OffsetDateTime.now();
            return this;
        }

        public RoundBuilder sortNumber() {
            if (state == RoundState.FINISHED) throw new RoundAlreadyFinishedException("Rodada já foi terminada");
            if (state == RoundState.CREATED) this.state = RoundState.INITIATED;
            var random = new Random();
            int preSortedNumber;
            do {
                preSortedNumber = random.nextInt(100);
            } while (sortedNumbers.contains(preSortedNumber));
            Integer sortedNumber = preSortedNumber;
            this.sortedNumbers.add(sortedNumber);
            this.bingoCards = bingoCards.stream()
                    .map(bingoCard -> bingoCard.toBuilder().incrementHintCountIfContains(sortedNumber).build())
                    .toList();
            this.updatedAt = OffsetDateTime.now();
            return this;
        }

        public RoundBuilder finish() {
            if (state == RoundState.FINISHED) throw new RoundAlreadyFinishedException("Rodada já foi terminada");
            if (state == RoundState.INITIATED) throw new RoundNotInitiatedException("Rodada ainda não foi iniciada");
            this.state = RoundState.FINISHED;
            this.updatedAt = OffsetDateTime.now();
            return this;
        }

        public Round build() {
            return new Round(id, bingoCards, sortedNumbers, state, createdAt, updatedAt);
        }

    }

}
