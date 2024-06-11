package me.dio.hiokdev.reactive_bingo.infractructure.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageableRoundsRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.BingoCardResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedRoundsResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.SortedNumberResponse;
import me.dio.hiokdev.reactive_bingo.application.ports.RoundUseCases;
import me.dio.hiokdev.reactive_bingo.core.validation.MongoId;
import me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.documentation.RoundControllerDoc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("rounds")
@RequiredArgsConstructor
public class RoundController implements RoundControllerDoc {

    private final RoundUseCases roundUseCases;

    @Override
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RoundResponse> create() {
        return roundUseCases.create();
    }

    @Override
    @PostMapping(value = "{id}/generate-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SortedNumberResponse> generateNextNumber(
            @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id
    ) {
        return roundUseCases.generateNextNumber(id);
    }

    @Override
    @PostMapping(value = "{id}/bingo-card/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<BingoCardResponse> generateBingoCard(
            @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id,
            @PathVariable @Valid @MongoId(message = "{playerController.id}") final String playerId
    ) {
        return roundUseCases.generateBingoCard(id, playerId);
    }

    @Override
    @GetMapping(value = "{id}/current-number", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SortedNumberResponse> getLastSortedNumber(
            @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id
    ) {
        return roundUseCases.getLastSortedNumber(id);
    }

    @Override
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RoundResponse> findById(
            @PathVariable @Valid @MongoId(message = "{roundController.id}") final String id
    ) {
        return roundUseCases.findById(id);
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<RoundResponse> findAll() {
        return roundUseCases.findAll();
    }

    @Override
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PagedRoundsResponse> findOnDemand(@Valid final PageableRoundsRequest request) {
        return roundUseCases.findOnDemand(request);
    }

}
