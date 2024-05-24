package me.dio.hiokdev.reactive_bingo.domain.models;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

    public Boolean checkIsValid(List<BingoCard> bingoCards) {
        for (BingoCard bingoCard : bingoCards) {
            if (countDuplicates(this.numbers, bingoCard.numbers) > 5) {
                return false;
            }
        }
        return true;
    }

    public Boolean isCompleted() {
        return hintCount == 20;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class BingoCardBuilder {

        private String id;
        private Player player;
        private List<Integer> numbers;
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

        public BingoCardBuilder generate(final Player player) {
            this.id = ObjectId.get().toString();
            this.player = player;
            this.numbers = new ArrayList<>();
            var random = new Random();
            for(var i = 0; i < 20; i++) {
                int preSortedNumber;
                do {
                    preSortedNumber = random.nextInt(100);
                } while (numbers.contains(preSortedNumber));
                this.numbers.add(preSortedNumber);
            }
            this.hintCount = 0;
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
            return this;
        }

        public BingoCardBuilder incrementHintCountIfContains(final Integer sortedNumber) {
            if (numbers.contains(sortedNumber)) {
                ++ this.hintCount;
                this.updatedAt = OffsetDateTime.now();
            }
            return this;
        }

        public BingoCard build() {
            return new BingoCard(id, player, numbers, hintCount, createdAt, updatedAt);
        }

    }

    private int countDuplicates(List<Integer> list1, List<Integer> list2) {
        Set<Integer> set2 = new HashSet<>(list2);
        int count = 0;
        for (int num : list1) {
            if (set2.contains(num)) {
                count++;
            }
        }
        return count;
    }

}
