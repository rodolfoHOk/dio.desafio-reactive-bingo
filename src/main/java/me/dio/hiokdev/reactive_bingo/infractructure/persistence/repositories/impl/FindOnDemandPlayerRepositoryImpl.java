package me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.utils.QueryBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FindOnDemandPlayerRepositoryImpl {

    private final ReactiveMongoTemplate template;
    private final QueryBuilder queryBuilder;

    public Flux<PlayerDocument> findOnDemand(final PageablePlayers pageable) {
        return queryBuilder.buildWhere(new Query(), pageable.sentence(), List.of("name", "email"))
                .map(query -> query
                        .with(pageable.sortDirection() == SortDirection.ASC
                                ? Sort.by(pageable.sortBy().getField()).ascending()
                                : Sort.by(pageable.sortBy().getField()).descending())
                        .skip(pageable.getSkip())
                        .limit(pageable.limit()))
                .flatMapMany(query -> template.find(query, PlayerDocument.class));
    }

    public Mono<Long> count(final PageablePlayers pageable) {
        return queryBuilder.buildWhere(new Query(), pageable.sentence(), List.of("name", "email"))
                .flatMap(query -> template.count(query, PlayerDocument.class));
    }

}
