package me.dio.hiokdev.reactive_bingo.domain.services;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.RoundQueryService;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoundService {

    private final PlayerQueryService playerQueryService;
    private final RoundQueryService roundQueryService;
    private final RoundGateway roundGateway;

    public Mono<Round> create() {
        return Round.builder().create()
                .map(Round.RoundBuilder::build)
                .flatMap(roundGateway::save);
    }

    public Mono<Integer> generateNextNumber(final String id) {
        return roundQueryService.findById(id)
                .flatMap(round -> round.toBuilder().sortNumber())
                .map(Round.RoundBuilder::build)
                .flatMap(roundGateway::save)
                .flatMap(this::processIfHasWinners);
    }

    public Mono<BingoCard> generateBingoCard(final String id, final String playerId) {
        return roundQueryService.verifyIfExistsByIdAndPlayerId(id, playerId)
                .flatMap(unused -> playerQueryService.findById(playerId))
                .zipWhen(player -> roundQueryService.findById(id))
                .flatMap(tuple -> tuple.getT2().toBuilder().addBingoCard(tuple.getT1()))
                .map(Round.RoundBuilder::build)
                .flatMap(roundGateway::save)
                .map(round -> round.bingoCards().stream()
                        .filter(bingoCard -> bingoCard.player().id().equals(playerId))
                        .toList()
                        .getFirst());
    }

    private Mono<Integer> processIfHasWinners(final Round round) {
        if (!round.winnersIds().isEmpty()) {
            // TODO notify by email round result
        }
        return round.getLastSortedNumber();
    }

}
