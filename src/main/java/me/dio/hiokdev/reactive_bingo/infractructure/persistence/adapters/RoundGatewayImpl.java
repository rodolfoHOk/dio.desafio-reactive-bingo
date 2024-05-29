package me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedRounds;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.RoundDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.RoundDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandRoundRepositoryImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoundGatewayImpl implements RoundGateway {

    private final RoundDocumentRepository roundDocumentRepository;
    private final FindOnDemandRoundRepositoryImpl findOnDemandRoundRepository;
    private final RoundDocumentMapper roundDocumentMapper;

    @Override
    public Mono<Round> save(final Round round) {
        return roundDocumentRepository.save(roundDocumentMapper.toDocument(round))
                .map(roundDocumentMapper::toDomainModel);
    }

    @Override
    public Flux<Round> findAll() {
        return roundDocumentRepository.findAll()
                .map(roundDocumentMapper::toDomainModel);
    }

    @Override
    public Mono<PagedRounds> findOnDemand(final PageableRounds pageable) {
        return findOnDemandRoundRepository.findOnDemand(pageable)
                .collectList()
                .zipWhen(documents -> findOnDemandRoundRepository.count(pageable))
                .map(tuple -> PagedRounds.builder()
                        .currentPage(pageable.page())
                        .totalPages((tuple.getT2() / pageable.limit())
                                + (((tuple.getT2()) % pageable.limit() > 0) ? 1 : 0))
                        .totalItens(tuple.getT2())
                        .content(tuple.getT1().stream().map(roundDocumentMapper::toDomainModel).toList())
                        .build());
    }

    @Override
    public Mono<Round> findById(final String id) {
        return roundDocumentRepository.findById(id)
                .map(roundDocumentMapper::toDomainModel);
    }

    @Override
    public Mono<Boolean> existsByIdAndPlayerId(final String id, final String playerId) {
        return roundDocumentRepository.existsByIdAndBingoCards_Player_Id(id, playerId);
    }

}
