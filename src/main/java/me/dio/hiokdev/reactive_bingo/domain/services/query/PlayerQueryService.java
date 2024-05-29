package me.dio.hiokdev.reactive_bingo.domain.services.query;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedPlayers;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.PlayerGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
public class PlayerQueryService {

    private final PlayerGateway playerGateway;

    public Mono<Player> findById(final String id) {
        return playerGateway.findById(id)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono
                        .error(new NotFoundException("Jogador não encontrado com identificador informado: "
                                + id))));
    }

    public Mono<Player> findByEmail(final String email) {
        return playerGateway.findByEmail(email)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono
                        .error(new NotFoundException("Jogador não encontrado com e-mail informado: "
                                + email))));
    }

    public Mono<PagedPlayers> findOnDemand(final PageablePlayers pageable) {
        return playerGateway.findOnDemand(pageable);
    }

}
