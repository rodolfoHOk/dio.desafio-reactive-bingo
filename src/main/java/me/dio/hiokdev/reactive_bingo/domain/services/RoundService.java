package me.dio.hiokdev.reactive_bingo.domain.services;

import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.RoundQueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
public class RoundService {

    private final PlayerQueryService playerQueryService;
    private final RoundQueryService roundQueryService;
    private final RoundGateway roundGateway;
    private final MailService mailService;

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
                .flatMap(round -> round.winnersIds().isEmpty()
                        ? round.getLastSortedNumber()
                        : round.getLastSortedNumber()
                        .onTerminateDetach()
                        .doOnSuccess(lastSortedNumber -> processIfHasWinners(round)));
    }

    public Mono<BingoCard> generateBingoCard(final String id, final String playerId) {
        return roundQueryService.verifyIfExistsByIdAndPlayerId(id, playerId)
                .then(Mono.defer(() -> playerQueryService.findById(playerId)))
                .zipWhen(player -> roundQueryService.findById(id))
                .flatMap(tuple -> tuple.getT2().toBuilder().addBingoCard(tuple.getT1()))
                .map(Round.RoundBuilder::build)
                .flatMap(roundGateway::save)
                .map(round -> round.bingoCards().stream()
                        .filter(bingoCard -> bingoCard.player().id().equals(playerId))
                        .toList()
                        .getFirst());
    }

    private void processIfHasWinners(final Round round) {
        Flux.fromIterable(round.bingoCards())
                .flatMap(bingoCard -> round.winnersIds().contains(bingoCard.player().id())
                        ? notifyWinner(round, bingoCard)
                        : notifyPlayer(round, bingoCard))
                .subscribeOn(Schedulers.parallel())
                .subscribe();
    }

    private Mono<Void> notifyPlayer(final Round round, final BingoCard bingoCard) {
        return playerQueryService.findById(bingoCard.player().id())
                .map(player -> MailMessage.create(round, player, bingoCard))
                .flatMap(mailService::send);
    }

    private Mono<Void> notifyWinner(final Round round, final BingoCard bingoCard) {
        return playerQueryService.findById(bingoCard.player().id())
                .map(player -> MailMessage.createWinner(round, player, bingoCard))
                .flatMap(mailService::send);
    }

}
