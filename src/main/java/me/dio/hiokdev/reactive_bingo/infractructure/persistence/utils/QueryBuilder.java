package me.dio.hiokdev.reactive_bingo.infractructure.persistence.utils;

import me.dio.hiokdev.reactive_bingo.core.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

@Component
public class QueryBuilder {

    public Mono<Query> buildWhere(final Query query, final String sentence, final String field) {
        return Mono.just(query)
                .filter(q -> StringUtils.isNotBlank(sentence))
                .switchIfEmpty(Mono.just(query))
                .flatMap(q -> setWhereClause(q, Criteria.where(field).regex(sentence, "i")));
    }

    public Mono<Query> buildWhere(final Query query, final String sentence, final List<String> fields) {
        return Mono.just(query)
                .filter(q -> StringUtils.isNotBlank(sentence))
                .switchIfEmpty(Mono.just(query))
                .flatMapMany(q -> Flux.fromIterable(fields))
                .map(field -> Criteria.where(field).regex(sentence, "i"))
                .collectList()
                .flatMap(criteriaList -> setWhereClause(query, criteriaList));
    }

    public Mono<Query> buildDateCriteria(
            final Query query,
            final String dateFieldName,
            final OffsetDateTime startDate,
            final OffsetDateTime endDate
    ) {
        return Mono.fromCallable(() -> {
            Date start = DateUtils.toDate(startDate);
            Date end = DateUtils.toDate(endDate);
            Criteria dateCriteria = Criteria.where(dateFieldName).gte(start).lte(end);
            return query.addCriteria(dateCriteria);
        });
    }

    private Mono<Query> setWhereClause(final Query query, final Criteria criteria) {
        return Mono.fromCallable(() -> query.addCriteria(criteria));
    }

    private Mono<Query> setWhereClause(final Query query, final List<Criteria> criteriaList) {
        return Mono.fromCallable(() -> {
            Criteria whereClause = new Criteria().orOperator(criteriaList);
            return query.addCriteria(whereClause);
        });
    }

}
