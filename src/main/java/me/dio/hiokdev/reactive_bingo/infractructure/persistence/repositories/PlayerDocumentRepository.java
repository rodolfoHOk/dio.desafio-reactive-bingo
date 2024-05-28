package me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories;

import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PlayerDocumentRepository extends ReactiveMongoRepository<PlayerDocument, String> {

}
