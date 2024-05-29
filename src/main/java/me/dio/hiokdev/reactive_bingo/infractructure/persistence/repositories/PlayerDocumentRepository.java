package me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories;

import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PlayerDocumentRepository extends ReactiveMongoRepository<PlayerDocument, String> {

    Mono<PlayerDocument> findByEmail(String email);

}
