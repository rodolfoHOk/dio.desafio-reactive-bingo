package me.dio.hiokdev.reactive_bingo.domain.gateways;

import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedPlayers;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import reactor.core.publisher.Mono;

public interface PlayerGateway {

    Mono<Player> save(Player player);

    Mono<PagedPlayers> findOnDemand(PageablePlayers pageable);

    Mono<Player> findById(String id);

    Mono<Void> delete(Player player);

}
