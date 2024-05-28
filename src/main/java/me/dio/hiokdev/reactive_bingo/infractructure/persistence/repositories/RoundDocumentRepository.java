package me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories;

import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RoundDocumentRepository extends ReactiveMongoRepository<RoundDocument, String> {

    Mono<Boolean> existsByIdAndBingoCards_Player_Id(String id, String playerId);

}
