package me.dio.hiokdev.reactive_bingo.domain.services;


import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.EmailAlreadyUsedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.PlayerGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RequiredArgsConstructor
public class PlayerService {

    private final PlayerQueryService playerQueryService;
    private final PlayerGateway playerGateway;

    public Mono<Player> save(Player player) {
        return playerQueryService.findByEmail(player.email())
                .filter(Objects::isNull)
                .switchIfEmpty(Mono.defer(() -> Mono
                        .error(new EmailAlreadyUsedException("Já existe jogador cadastro com o e-mail informado: "
                                + player.email()))))
                .onErrorResume(NotFoundException.class, e -> playerGateway.save(player));
    }

    public Mono<Player> update(Player player) {
        return verifyEmail(player)
                .flatMap(unused -> playerQueryService.findById(player.id()))
                .map(existingPlayer -> player.toBuilder()
                        .createdAt(existingPlayer.createdAt())
                        .updatedAt(existingPlayer.updatedAt())
                        .build())
                .flatMap(playerGateway::save);
    }

    public Mono<Void> delete(String id) {
        return playerQueryService.findById(id)
                .flatMap(playerGateway::delete);
    }

    public Mono<Player> verifyEmail(final Player player) {
        return playerQueryService.findByEmail(player.email())
                .filter(existingPlayer -> player.id().equals(existingPlayer.id()))
                .switchIfEmpty(Mono.defer(() -> Mono
                        .error(new EmailAlreadyUsedException("Já existe jogador cadastro com o e-mail informado: "
                                + player.email()))))
                .onErrorResume(NotFoundException.class, e -> Mono.empty())
                .flatMap(existingPlayer -> Mono.empty());
    }

}
