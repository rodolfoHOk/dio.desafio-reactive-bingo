package me.dio.hiokdev.reactive_bingo.infractructure.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageablePlayersRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedPlayersResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PlayerResponse;
import me.dio.hiokdev.reactive_bingo.application.ports.PlayerUseCases;
import me.dio.hiokdev.reactive_bingo.core.validation.MongoId;
import me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.documentation.PlayerControllerDoc;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("players")
@RequiredArgsConstructor
public class PlayerController implements PlayerControllerDoc {

    private final PlayerUseCases playerUseCases;

    @Override
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<PlayerResponse> create(@RequestBody @Valid final PlayerRequest request) {
        return playerUseCases.create(request);
    }

    @Override
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PlayerResponse> update(
            @PathVariable @Valid @MongoId(message = "{playerController.id}") final String id,
            @RequestBody @Valid final PlayerRequest request
    ) {
        return playerUseCases.update(id, request);
    }

    @Override
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable @Valid @MongoId(message = "{playerController.id}") final String id) {
        return playerUseCases.delete(id);
    }

    @Override
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PlayerResponse> findById(@PathVariable @Valid @MongoId(message = "{playerController.id}") final String id) {
        return playerUseCases.findById(id);
    }

    @Override
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<PagedPlayersResponse> findOnDemand(@Valid final PageablePlayersRequest request) {
        return playerUseCases.findOnDemand(request);
    }

}
