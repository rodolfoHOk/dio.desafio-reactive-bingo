package me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories;

import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RoundDocumentRepository extends ReactiveMongoRepository<RoundDocument, String> {

}
