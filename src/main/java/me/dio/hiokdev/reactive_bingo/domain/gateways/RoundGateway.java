package me.dio.hiokdev.reactive_bingo.domain.gateways;

import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedRounds;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoundGateway {

    Mono<Round> save(Round round);

    Flux<Round> findAll();

    Mono<PagedRounds> findOnDemand(PageableRounds pageable);

    Mono<Round> findById(String id);

}
