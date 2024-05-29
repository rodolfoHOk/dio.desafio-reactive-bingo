package me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.List;

@Document(collection = "rounds")
public record RoundDocument(
        @Id String id,
        @Field("bingo_cards") List<BingoCardSubDocument> bingoCards,
        @Field("sorted_numbers") List<Integer> sortedNumbers,
        @Field("winners_ids") List<String> winnersIds,
        RoundStateEnum state,
        @CreatedDate @Field("created_at") OffsetDateTime createdAt,
        @LastModifiedDate @Field("updated_at") OffsetDateTime updatedAt
) {

    @Builder(toBuilder = true)
    public RoundDocument {
    }

}
