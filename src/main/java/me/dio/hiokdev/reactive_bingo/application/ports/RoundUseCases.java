package me.dio.hiokdev.reactive_bingo.application.ports;

import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageableRoundsRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.BingoCardResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedRoundsResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.SortedNumberResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoundUseCases {

    Mono<RoundResponse> create();

    Mono<SortedNumberResponse> generateNextNumber(String id);

    Mono<BingoCardResponse> generateBingoCard(String id, String playerId);

    Mono<SortedNumberResponse> getLastSortedNumber(String id);

    Mono<RoundResponse> findById(String id);

    Flux<RoundResponse> findAll();

    Mono<PagedRoundsResponse> findOnDemand(PageableRoundsRequest request);

}
