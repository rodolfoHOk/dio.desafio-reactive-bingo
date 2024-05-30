package me.dio.hiokdev.reactive_bingo.application.usecases;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageableRoundsRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.BingoCardResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedRoundsResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.application.mappers.BingoCardMapper;
import me.dio.hiokdev.reactive_bingo.application.mappers.RoundMapper;
import me.dio.hiokdev.reactive_bingo.application.ports.RoundUseCases;
import me.dio.hiokdev.reactive_bingo.domain.services.RoundService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.RoundQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoundUseCasesImpl implements RoundUseCases {

    private final RoundService roundService;
    private final RoundQueryService roundQueryService;
    private final RoundMapper roundMapper;
    private final BingoCardMapper bingoCardMapper;

    @Override
    public Mono<RoundResponse> create() {
        return roundService.create()
                .map(roundMapper::toResponse);
    }

    @Override
    public Mono<Integer> generateNextNumber(final String id) {
        return roundService.generateNextNumber(id);
    }

    @Override
    public Mono<BingoCardResponse> generateBingoCard(final String id, final String playerId) {
        return roundService.generateBingoCard(id, playerId)
                .map(bingoCardMapper::toResponse);
    }

    @Override
    public Mono<Integer> getLastSortedNumber(final String id) {
        return roundQueryService.getLastSortedNumber(id);
    }

    @Override
    public Mono<RoundResponse> findById(final String id) {
        return roundQueryService.findById(id)
                .map(roundMapper::toResponse);
    }

    @Override
    public Flux<RoundResponse> findAll() {
        return roundQueryService.findAll()
                .map(roundMapper::toResponse);
    }

    @Override
    public Mono<PagedRoundsResponse> findOnDemand(final PageableRoundsRequest request) {
        return Mono.just(request)
                .map(roundMapper::toDomainDto)
                .flatMap(roundQueryService::findOnDemand)
                .map(roundMapper::toResponse);
    }

}
