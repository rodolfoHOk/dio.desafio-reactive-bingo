package me.dio.hiokdev.reactive_bingo.infrastructure.web.controllers;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.ReactiveBingoApplication;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.application.mappers.BingoCardMapperImpl;
import me.dio.hiokdev.reactive_bingo.application.mappers.RoundMapperImpl;
import me.dio.hiokdev.reactive_bingo.application.usecases.RoundUseCasesImpl;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.mongo.OffsetDateTimeProvider;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
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

}
