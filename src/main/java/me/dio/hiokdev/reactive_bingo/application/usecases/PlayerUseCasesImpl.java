package me.dio.hiokdev.reactive_bingo.application.usecases;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageablePlayersRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedPlayersResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PlayerResponse;
import me.dio.hiokdev.reactive_bingo.application.mappers.PlayerMapper;
import me.dio.hiokdev.reactive_bingo.application.ports.PlayerUseCases;
import me.dio.hiokdev.reactive_bingo.domain.services.PlayerService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PlayerUseCasesImpl implements PlayerUseCases {

    private final PlayerService playerService;
    private final PlayerQueryService playerQueryService;
    private final PlayerMapper playerMapper;

    @Override
    public Mono<PlayerResponse> create(final PlayerRequest request) {
        return Mono.just(request)
                .map(playerMapper::toDomainModel)
                .flatMap(playerService::save)
                .map(playerMapper::toResponse);
    }

    @Override
    public Mono<PlayerResponse> update(final String id, final PlayerRequest request) {
        return Mono.just(request)
                .map(requestBody -> playerMapper.toDomainModel(requestBody, id))
                .flatMap(playerService::update)
                .map(playerMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(final String id) {
        return playerService.delete(id);
    }

    @Override
    public Mono<PlayerResponse> findById(final String id) {
        return playerQueryService.findById(id)
                .map(playerMapper::toResponse);
    }

    @Override
    public Mono<PagedPlayersResponse> findOnDemand(final PageablePlayersRequest request) {
        return Mono.just(request)
                .map(playerMapper::toDomainDto)
                .flatMap(playerQueryService::findOnDemand)
                .map(playerMapper::toResponse);
    }

}
