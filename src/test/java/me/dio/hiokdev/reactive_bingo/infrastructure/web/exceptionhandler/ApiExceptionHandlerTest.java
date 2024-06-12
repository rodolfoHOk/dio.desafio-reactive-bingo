package me.dio.hiokdev.reactive_bingo.infrastructure.web.exceptionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.dio.hiokdev.reactive_bingo.ReactiveBingoApplication;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.ProblemResponse;
import me.dio.hiokdev.reactive_bingo.application.mappers.PlayerMapperImpl;
import me.dio.hiokdev.reactive_bingo.application.usecases.PlayerUseCasesImpl;
import me.dio.hiokdev.reactive_bingo.core.mongo.OffsetDateTimeProvider;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.ReactiveBingoException;
import me.dio.hiokdev.reactive_bingo.domain.services.PlayerService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.PlayerController;
import me.dio.hiokdev.reactive_bingo.infractructure.web.expectionhandler.ApiExceptionHandler;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(PlayerController.class)
@ContextConfiguration(classes = {ReactiveBingoApplication.class, ApiExceptionHandler.class, MongoMappingContext.class,
        OffsetDateTimeProvider.class, PlayerUseCasesImpl.class, PlayerMapperImpl.class})
public class ApiExceptionHandlerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerQueryService playerQueryService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup(ApplicationContext applicationContext) {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .responseTimeout(Duration.ofMinutes(1))
                .build();
    }

    @Test
    void handleMethodNotAllowedExceptionTest() {
        this.webTestClient.patch()
                .uri("/players")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
                });
    }

    @Test
    void handleResponseStatusExceptionTest() {
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new ResponseStatusException(HttpStatusCode.valueOf(400))));

        this.webTestClient.get()
                .uri("/players" + ObjectId.get())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
    }

    @Test
    void handleReactiveBingoExceptionTest() {
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new ReactiveBingoException("")));

        this.webTestClient.get()
                .uri("/players/"  + ObjectId.get())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                });
    }

    @Test
    void handleJsonProcessingExceptionTest() {
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new MockJsonProcessingException("")));

        this.webTestClient.get()
                .uri("/players/" + ObjectId.get())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
    }

    @Test
    void handleExceptionTest() {
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new Exception("")));

        this.webTestClient.get()
                .uri("/players/" + ObjectId.get())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                });
    }

    private static class MockJsonProcessingException extends JsonProcessingException {
        public MockJsonProcessingException(String message) {
            super(message);
        }
    }

}
