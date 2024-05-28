package me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents;

import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

public record BingoCardSubDocument(
        @Field("bingo_card_id") String bingoCardId,
        PlayerDocument player,
        List<Integer> numbers,
        @Field("hint_count") Integer hintCount,
        @Field("created_at") OffsetDateTime createdAt,
        @Field("updated_at") OffsetDateTime updatedAt
) {

    @Builder(toBuilder = true)
    public BingoCardSubDocument {
    }

}
