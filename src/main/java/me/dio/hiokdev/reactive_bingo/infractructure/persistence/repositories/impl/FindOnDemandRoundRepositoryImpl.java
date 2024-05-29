package me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.utils.QueryBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class FindOnDemandRoundRepositoryImpl {

    private final ReactiveMongoTemplate template;
    private final QueryBuilder queryBuilder;

    public Flux<RoundDocument> findOnDemand(final PageableRounds pageable) {
        return queryBuilder.buildWhere(new Query(), pageable.sentence(), "state")
                .flatMap(query -> queryBuilder
                        .buildDateCriteria(query, "createdAt", pageable.startDate(), pageable.endDate()))
                .map(query -> query
                        .with(pageable.sortDirection() == SortDirection.ASC
                                ? Sort.by(pageable.sortBy().getField()).ascending()
                                : Sort.by(pageable.sortBy().getField()).descending())
                        .skip(pageable.getSkip())
                        .limit(pageable.limit()))
                .flatMapMany(query -> template.find(query, RoundDocument.class));
    }

    public Mono<Long> count(final PageableRounds pageable) {
        return queryBuilder.buildWhere(new Query(), pageable.sentence(), "state")
                .flatMap(query -> queryBuilder
                        .buildDateCriteria(query, "createdAt", pageable.startDate(), pageable.endDate()))
                .map(query -> query
                        .with(pageable.sortDirection() == SortDirection.ASC
                                ? Sort.by(pageable.sortBy().getField()).ascending()
                                : Sort.by(pageable.sortBy().getField()).descending())
                        .skip(pageable.getSkip())
                        .limit(pageable.limit()))
                .flatMap(query -> template.count(query, RoundDocument.class));
    }

}
