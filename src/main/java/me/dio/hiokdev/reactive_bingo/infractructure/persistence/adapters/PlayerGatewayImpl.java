package me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedPlayers;
import me.dio.hiokdev.reactive_bingo.domain.gateways.PlayerGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.PlayerDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.PlayerDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandPlayerRepositoryImpl;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class PlayerGatewayImpl implements PlayerGateway {

    private final PlayerDocumentRepository playerDocumentRepository;
    private final FindOnDemandPlayerRepositoryImpl findOnDemandPlayerRepository;
    private final PlayerDocumentMapper playerDocumentMapper;

    @Override
    public Mono<Player> save(final Player player) {
        return playerDocumentRepository.save(playerDocumentMapper.toDocument(player))
                .map(playerDocumentMapper::toDomainModel);
    }

    @Override
    public Mono<PagedPlayers> findOnDemand(final PageablePlayers pageable) {
        return findOnDemandPlayerRepository.findOnDemand(pageable)
                .collectList()
                .zipWhen(documents -> findOnDemandPlayerRepository.count(pageable))
                .map(tuple -> PagedPlayers.builder()
                        .currentPage(pageable.page())
                        .totalPages((tuple.getT2() / pageable.limit())
                                + (((tuple.getT2()) % pageable.limit() > 0) ? 1 : 0))
                        .totalItens(tuple.getT2())
                        .content(tuple.getT1().stream().map(playerDocumentMapper::toDomainModel).toList())
                        .build());
    }

    @Override
    public Mono<Player> findById(final String id) {
        return playerDocumentRepository.findById(id)
                .map(playerDocumentMapper::toDomainModel);
    }

    @Override
    public Mono<Player> findByEmail(final String email) {
        return playerDocumentRepository.findByEmail(email)
                .map(playerDocumentMapper::toDomainModel);
    }

    @Override
    public Mono<Void> delete(final Player player) {
        return playerDocumentRepository.delete(playerDocumentMapper.toDocument(player));
    }

}
