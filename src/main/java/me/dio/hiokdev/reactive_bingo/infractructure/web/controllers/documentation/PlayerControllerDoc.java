package me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PageablePlayersRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedPlayersResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PlayerResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.ProblemResponse;
import me.dio.hiokdev.reactive_bingo.core.validation.MongoId;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

@Tag(name = "Players", description = "Endpoints para gerenciar jogadores")
public interface PlayerControllerDoc {

    @Operation(summary = "Cria novo jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Retorna jogador criado"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    Mono<PlayerResponse> create(@RequestBody @Valid PlayerRequest request);

    @Operation(summary = "Atualiza dados do jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna jogador atualizado"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @PutMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<PlayerResponse> update(
            @Parameter(description = "Identificador do jogador", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{playerController.id}") String id,
            @RequestBody @Valid PlayerRequest request
    );

    @Operation(summary = "Remove jogador pelo identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No content / Sem conteúdo"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> delete(
            @Parameter(description = "Identificador do jogador", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{playerController.id}") String id
    );

    @Operation(summary = "Busca jogador pelo identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna jogador correspondente"),
            @ApiResponse(responseCode = "400", description = "Bad request / Requisição inválida",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found / Não encontrado",
                    content = @Content(schema = @Schema(implementation = ProblemResponse.class)))
    })
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<PlayerResponse> findById(
            @Parameter(description = "Identificador do jogador", example = "66342b8418c87a1a8a8ffcb0")
            @PathVariable @Valid @MongoId(message = "{playerController.id}") String id
    );

    @Operation(summary = "Buscar jogadores de forma paginada")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retorna jogadores de acordo com as informações passadas na requisição")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<PagedPlayersResponse> findOnDemand(@ParameterObject @Valid PageablePlayersRequest request);

}
