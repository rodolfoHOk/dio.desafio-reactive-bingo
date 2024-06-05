package me.dio.hiokdev.reactive_bingo.core.factory.document;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.BingoCardSubDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundStateEnum;

import java.time.OffsetDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoundDocumentFactory {

    public static RoundDocumentFactoryBuilder builder(final Round round) {
        return new RoundDocumentFactoryBuilder(round);
    }

    public static class RoundDocumentFactoryBuilder {

        private String id;
        private List<BingoCardSubDocument> bingoCards;
        private List<Integer> sortedNumbers;
        private List<String> winnersIds;
        private RoundStateEnum state;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public RoundDocumentFactoryBuilder(final Round round) {
            this.id = round.id();
            this.bingoCards = round.bingoCards().stream().map(this::toBingoCardDocument).toList();
            this.sortedNumbers = round.sortedNumbers();
            this.winnersIds = round.winnersIds();
            this.state = RoundStateEnum.valueOf(round.state().name());
            this.createdAt = round.createdAt();
            this.updatedAt = round.updatedAt();
        }

        public RoundDocument build() {
            return RoundDocument.builder()
                    .id(id)
                    .bingoCards(bingoCards)
                    .sortedNumbers(sortedNumbers)
                    .winnersIds(winnersIds)
                    .state(state)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

        private BingoCardSubDocument toBingoCardDocument(final BingoCard bingoCard) {
            return BingoCardSubDocument.builder()
                    .bingoCardId(bingoCard.id())
                    .player(PlayerDocumentFactory.builder(bingoCard.player()).build())
                    .numbers(bingoCard.numbers())
                    .hintCount(bingoCard.hintCount())
                    .createdAt(bingoCard.createdAt())
                    .updatedAt(bingoCard.updatedAt())
                    .build();
        }

    }

}
