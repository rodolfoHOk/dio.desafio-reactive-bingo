package me.dio.hiokdev.reactive_bingo.application.ports;

import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageablePlayersRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedPlayersResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PlayerResponse;
import reactor.core.publisher.Mono;

public interface PlayersUseCases {

    Mono<PlayerResponse> save(PlayerRequest request);

    Mono<PlayerResponse> update(String id, PlayerRequest request);

    Mono<Void> delete(String id);

    Mono<PlayerResponse> findById(String id);

    Mono<PagedPlayersResponse> findOnDemand(PageablePlayersRequest request);

}
