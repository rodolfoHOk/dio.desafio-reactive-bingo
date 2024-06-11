package me.dio.hiokdev.reactive_bingo.infrastructure.web.controllers;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.ReactiveBingoApplication;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.BingoCardResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.FieldErrorResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.ProblemResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.SortedNumberResponse;
import me.dio.hiokdev.reactive_bingo.application.mappers.BingoCardMapperImpl;
import me.dio.hiokdev.reactive_bingo.application.mappers.RoundMapperImpl;
import me.dio.hiokdev.reactive_bingo.application.usecases.RoundUseCasesImpl;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.dto.PagedRoundsFactory;
import me.dio.hiokdev.reactive_bingo.core.mongo.OffsetDateTimeProvider;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BingoCardAlreadyExistsException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RecursionException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyFinishedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundNotInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.domain.services.RoundService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.RoundQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.RoundController;
import me.dio.hiokdev.reactive_bingo.infractructure.web.expectionhandler.ApiExceptionHandler;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(RoundController.class)
@ContextConfiguration(classes = {ReactiveBingoApplication.class, ApiExceptionHandler.class, MongoMappingContext.class,
        OffsetDateTimeProvider.class, RoundUseCasesImpl.class, RoundMapperImpl.class, BingoCardMapperImpl.class})
public class RoundControllerTest {

    @MockBean
    private RoundService roundService;
    @MockBean
    private RoundQueryService roundQueryService;

    private WebTestClient webTestClient;

    private static final Faker faker = FakerData.getFaker();

    @BeforeEach
    void setup(ApplicationContext applicationContext) {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .responseTimeout(Duration.ofMinutes(1))
                .build();
    }

    @Test
    void whenCreateThenReturnCreated() {
        var round = Objects.requireNonNull(Round.builder().create().block())
                .id(ObjectId.get().toString())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        when(roundService.create()).thenReturn(Mono.just(round));

        this.webTestClient
                .post()
                .uri("/rounds")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoundResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.state()).isEqualTo("CREATED");
                    assertThat(responseBody.bingoCards()).isEmpty();
                    assertThat(responseBody.sortedNumbers()).isEmpty();
                    assertThat(responseBody.winnersIds()).isEmpty();
                });
        verify(roundService, times(1)).create();
    }

    @Test
    void whenGenerateNextNumberThenReturnOk() {
        var generatedNumber = faker.number().numberBetween(1, 99);
        when(roundService.generateNextNumber(anyString())).thenReturn(Mono.just(generatedNumber));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/generate-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SortedNumberResponse.class)
                .value(responseBody -> assertThat(responseBody.sortedNumber()).isEqualTo(generatedNumber));
        verify(roundService, times(1)).generateNextNumber(anyString());
    }

    @Test
    void whenGenerateNextNumberWithNonExistingIdThenReturnNotFound() {
        when(roundService.generateNextNumber(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/generate-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
        verify(roundService, times(1)).generateNextNumber(anyString());
    }

    @Test
    void whenGenerateNextNumberWithInvalidIdThenReturnBadRequest() {
        var invalidId = faker.lorem().word();

        this.webTestClient
                .post()
                .uri("/rounds/" + invalidId + "/generate-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains("id");
                });
        verify(roundService, times(0)).generateNextNumber(anyString());
    }

    @Test
    void whenGenerateNextNumberWithFinishedRoundIdThenReturnBadRequest() {
        when(roundService.generateNextNumber(anyString())).thenReturn(Mono.error(new RoundAlreadyFinishedException("")));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/generate-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
        verify(roundService, times(1)).generateNextNumber(anyString());
    }

    @Test
    void whenGenerateBingoCardThenReturnOk() {
        var player = PlayerFactory.builder().build();
        var generatedBingoCard = Objects.requireNonNull(BingoCard.builder().generate(player, List.of()).block()).build();
        when(roundService.generateBingoCard(anyString(), anyString())).thenReturn(Mono.just(generatedBingoCard));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/bingo-card/" + player.id())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BingoCardResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.playerId()).isEqualTo(player.id());
                    assertThat(responseBody.numbers()).containsExactlyInAnyOrderElementsOf(generatedBingoCard.numbers());
                    assertThat(responseBody.hintCount()).isEqualTo(0);
                });
        verify(roundService, times(1)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGenerateBingoCardWithInvalidRoundIdThenReturnBadRequest() {
        var invalidRoundId = faker.lorem().word();
        var playerId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + invalidRoundId + "/bingo-card/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains("id");
                });
        verify(roundService, times(0)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGenerateBingoCardWithInvalidPlayerIdThenReturnBadRequest() {
        var roundId = ObjectId.get().toString();
        var invalidPlayerId = faker.lorem().word();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/bingo-card/" + invalidPlayerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains("playerId");
                });
        verify(roundService, times(0)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGenerateBingoCardWithPlayerAlreadyHasBingoCardThenReturnBadRequest() {
        when(roundService.generateBingoCard(anyString(), anyString())).thenReturn(Mono.error(new BingoCardAlreadyExistsException("")));
        var roundId = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/bingo-card/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
        verify(roundService, times(1)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGenerateBingoCardWithRoundHasAlreadyInitiatedThenReturnBadRequest() {
        when(roundService.generateBingoCard(anyString(), anyString())).thenReturn(Mono.error(new RoundAlreadyInitiatedException("")));
        var roundId = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/bingo-card/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
        verify(roundService, times(1)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGenerateBingoCardAndHasManyBingoCardsThenReturnLoopDetected() {
        when(roundService.generateBingoCard(anyString(), anyString())).thenReturn(Mono.error(new RecursionException("")));
        var roundId = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/bingo-card/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.LOOP_DETECTED.value());
                });
        verify(roundService, times(1)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGenerateBingoCardWithNonExistingIdOrPlayerIdThenReturnNotFound() {
        when(roundService.generateBingoCard(anyString(), anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var roundId = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();

        this.webTestClient
                .post()
                .uri("/rounds/" + roundId + "/bingo-card/" + playerId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
        verify(roundService, times(1)).generateBingoCard(anyString(), anyString());
    }

    @Test
    void whenGetLastSortedNumberThenReturnOk() {
        var lastSortedNumber = faker.number().numberBetween(1, 99);
        when(roundQueryService.getLastSortedNumber(anyString())).thenReturn(Mono.just(lastSortedNumber));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .get()
                .uri("/rounds/" + roundId + "/current-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SortedNumberResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.sortedNumber()).isEqualTo(lastSortedNumber);
                });
        verify(roundQueryService, times(1)).getLastSortedNumber(anyString());
    }

    @Test
    void whenGetLastSortedNumberWithInvalidIdThenReturnBadRequest() {
        var invalidRoundId = faker.lorem().word();

        this.webTestClient
                .get()
                .uri("/rounds/" + invalidRoundId + "/current-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains("id");
                });
        verify(roundQueryService, times(0)).getLastSortedNumber(anyString());
    }

    @Test
    void whenGetLastSortedNumberWithNonExistingIdThenReturnNotFound() {
        when(roundQueryService.getLastSortedNumber(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .get()
                .uri("/rounds/" + roundId + "/current-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
        verify(roundQueryService, times(1)).getLastSortedNumber(anyString());
    }

    @Test
    void whenGetLastSortedNumberWithNonSortedNumberThenReturnBadRequest() {
        when(roundQueryService.getLastSortedNumber(anyString())).thenReturn(Mono.error(new RoundNotInitiatedException("")));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .get()
                .uri("/rounds/" + roundId + "/current-number")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
        verify(roundQueryService, times(1)).getLastSortedNumber(anyString());
    }

    @Test
    void whenFindByIdThenReturnOk() {
        var round = RoundFactory.createRoundToFinish();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .get()
                .uri("/rounds/" + roundId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoundResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.id()).isEqualTo(round.id());
                    assertThat(responseBody.bingoCards().size()).isEqualTo(round.bingoCards().size());
                    assertThat(responseBody.bingoCards().get(0).playerId()).isEqualTo(round.bingoCards().get(0).player().id());
                    assertThat(responseBody.bingoCards().get(0).numbers()).containsExactlyInAnyOrderElementsOf(round.bingoCards().get(0).numbers());
                    assertThat(responseBody.bingoCards().get(0).hintCount()).isEqualTo(round.bingoCards().get(0).hintCount());
                    assertThat(responseBody.sortedNumbers()).containsExactlyElementsOf(round.sortedNumbers());
                    assertThat(responseBody.winnersIds()).containsExactlyElementsOf(round.winnersIds());
                });
        verify(roundQueryService, times(1)).findById(anyString());
    }

    @Test
    void whenFindByIdWithInvalidIdThenReturnBadRequest() {
        var invalidRoundId = faker.lorem().word();

        this.webTestClient
                .get()
                .uri("/rounds/" + invalidRoundId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains("id");
                });
        verify(roundQueryService, times(0)).getLastSortedNumber(anyString());
    }

    @Test
    void whenFindByIdWithNonExistingIdThenReturnNotFound() {
        when(roundQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var roundId = ObjectId.get().toString();

        this.webTestClient
                .get()
                .uri("/rounds/" + roundId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
        verify(roundQueryService, times(1)).findById(anyString());
    }

    @Test
    void whenFindAllThenReturnOkAndRounds() {
        var rounds = Stream.generate(() -> RoundFactory.builder().build()).limit(10).toList();
        when(roundQueryService.findAll()).thenReturn(Flux.fromIterable(rounds));

        this.webTestClient
                .get()
                .uri("/rounds")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RoundResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.size()).isEqualTo(rounds.size());
                });
        verify(roundQueryService, times(1)).findAll();
    }

    @Test
    void whenFindAllThenReturnOkAndEmpty() {
        when(roundQueryService.findAll()).thenReturn(Flux.empty());

        this.webTestClient
                .get()
                .uri("/rounds")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RoundResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.size()).isEqualTo(0);
                });
        verify(roundQueryService, times(1)).findAll();
    }

    @Test
    void whenFindOnDemandThenReturnOk() {
        var pagedRounds = PagedRoundsFactory.builder(10).build();
        when(roundQueryService.findOnDemand(any(PageableRounds.class))).thenReturn(Mono.just(pagedRounds));

        
    }

}
