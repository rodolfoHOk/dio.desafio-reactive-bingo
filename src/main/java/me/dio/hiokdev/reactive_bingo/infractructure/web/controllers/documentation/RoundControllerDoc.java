package me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageableRoundsRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.BingoCardResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedRoundsResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.ProblemResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.SortedNumberResponse;
import me.dio.hiokdev.reactive_bingo.core.validation.MongoId;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Rounds", description = "Endpoints para gerenciar rodadas")
public interface RoundControllerDoc {

    @Operation(summary = "Cria nova rodada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retorna rodada criada")
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    Mono<RoundResponse> create();

    @Operation(summary = "Gera próximo número sorteado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna o novo número sorteado"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @PostMapping(value = "{id}/generate-number", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<SortedNumberResponse> generateNextNumber(
            @Parameter(description = "Identificador da rodada", example = "66351f41c475b40f15b62591")
            @PathVariable @Valid @MongoId(message = "{roundController.id}") String id
    );

    @Operation(summary = "Gera novo cartão de bingo para o jogador informado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna o cartão de bingo gerado"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @PostMapping(value = "{id}/bingo-card/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<BingoCardResponse> generateBingoCard(
            @Parameter(description = "Identificador da rodada", example = "66351f41c475b40f15b62591")
            @PathVariable @Valid @MongoId(message = "{roundController.id}") String id,
            @Parameter(description = "Identificador do jogador", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{playerController.id}") String playerId
    );

    @Operation(summary = "Busca o último número sorteado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna o último número sorteado"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @GetMapping(value = "{id}/current-number", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<SortedNumberResponse> getLastSortedNumber(
            @Parameter(description = "Identificador da rodada", example = "66351f41c475b40f15b62591")
            @PathVariable @Valid @MongoId(message = "{roundController.id}") String id
    );

    @Operation(summary = "Busca rodada pelo identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna rodada correspondente"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<RoundResponse> findById(
            @Parameter(description = "Identificador da rodada", example = "66351f41c475b40f15b62591")
            @PathVariable @Valid @MongoId(message = "{roundController.id}") String id
    );

    @Operation(summary = "Busca todas as rodadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna todas as rodadas")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    Flux<RoundResponse> findAll();

    @Operation(summary = "Buscar rodadas de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna rodadas de acordo com as informações passadas na requisição")
    })
    @GetMapping(value = "search", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<PagedRoundsResponse> findOnDemand(@ParameterObject @Valid PageableRoundsRequest request);

}
