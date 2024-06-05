package me.dio.hiokdev.reactive_bingo.domain.services.query;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedRounds;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BaseErrorMessage;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BingoCardAlreadyExistsException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundNotInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
public class RoundQueryService {

    private final RoundGateway roundGateway;

    public Mono<Round> findById(final String id) {
        return roundGateway.findById(id)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(BaseErrorMessage
                        .ROUND_NOT_FOUND.params(id).getMessage()))));
    }

    public Flux<Round> findAll() {
        return roundGateway.findAll();
    }

    public Mono<PagedRounds> findOnDemand(final PageableRounds pageable) {
        return roundGateway.findOnDemand(pageable);
    }

    public Mono<Void> verifyIfExistsByIdAndPlayerId(final String id, final String playerId) {
        return roundGateway.existsByIdAndPlayerId(id, playerId)
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BingoCardAlreadyExistsException(BaseErrorMessage
                        .BINGO_CARD_ALREADY_EXISTS.params(playerId, id).getMessage()))))
                .flatMap(exists -> Mono.empty());
    }

    public Mono<Integer> getLastSortedNumber(final String id) {
        return this.findById(id)
                .flatMap(Round::getLastSortedNumber)
                .onErrorResume(NoSuchElementException.class, e -> Mono
                        .error(new RoundNotInitiatedException(BaseErrorMessage.ROUND_NOT_INITIATED.getMessage())));
    }

}
