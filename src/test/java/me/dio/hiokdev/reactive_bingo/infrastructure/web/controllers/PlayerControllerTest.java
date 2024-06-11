package me.dio.hiokdev.reactive_bingo.infrastructure.web.controllers;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.ReactiveBingoApplication;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.FieldErrorResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.ProblemResponse;
import me.dio.hiokdev.reactive_bingo.application.mappers.PlayerMapperImpl;
import me.dio.hiokdev.reactive_bingo.application.usecases.PlayerUseCasesImpl;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.request.PlayerRequestFactory;
import me.dio.hiokdev.reactive_bingo.core.mongo.OffsetDateTimeProvider;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.EmailAlreadyUsedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.domain.services.PlayerService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.web.controllers.PlayerController;
import me.dio.hiokdev.reactive_bingo.infractructure.web.expectionhandler.ApiExceptionHandler;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebFluxTest(PlayerController.class)
@ContextConfiguration(classes = {ReactiveBingoApplication.class, ApiExceptionHandler.class, MongoMappingContext.class,
        OffsetDateTimeProvider.class, PlayerUseCasesImpl.class, PlayerMapperImpl.class})
public class PlayerControllerTest {

    @MockBean
    private PlayerService playerService;
    @MockBean
    private PlayerQueryService playerQueryService;

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
        when(playerService.save(any(Player.class))).thenAnswer(invocationOnMock -> {
            var player = invocationOnMock.getArgument(0, Player.class);
            return Mono.just(player.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });
        var requestBody = PlayerFactory.builder().preInsert().build();

        webTestClient.post()
                .uri("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Player.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.id()).isNotNull();
                    assertThat(responseBody).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt", "updatedAt")
                            .isEqualTo(requestBody);
                });
        verify(playerService, times(1)).save(any(Player.class));
    }

    @Test
    void whenSaveWithEmailInUseThenReturnBadRequest() {
        when(playerService.save(any(Player.class))).thenReturn(Mono.error(new EmailAlreadyUsedException("")));
        var requestBody = PlayerFactory.builder().preInsert().build();

        webTestClient.post()
                .uri("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
        verify(playerService, times(1)).save(any(Player.class));
    }

    private static Stream<Arguments> whenCreateWithInvalidConstraintWhenReturnBadRequest() {
        return Stream.of(
                Arguments.of(PlayerRequestFactory.builder().nullName().build(), "name"),
                Arguments.of(PlayerRequestFactory.builder().blankName().build(), "name"),
                Arguments.of(PlayerRequestFactory.builder().shortName().build(), "name"),
                Arguments.of(PlayerRequestFactory.builder().longName().build(), "name"),
                Arguments.of(PlayerRequestFactory.builder().nullEmail().build(), "email"),
                Arguments.of(PlayerRequestFactory.builder().blankEmail().build(), "email"),
                Arguments.of(PlayerRequestFactory.builder().shortEmail().build(), "email"),
                Arguments.of(PlayerRequestFactory.builder().longEmail().build(), "email"),
                Arguments.of(PlayerRequestFactory.builder().invalidEmail().build(), "email")
        );
    }

    @MethodSource
    @ParameterizedTest
    void whenCreateWithInvalidConstraintWhenReturnBadRequest(final PlayerRequest requestBody, final String field) {
        webTestClient.post()
                .uri("/players")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains(field);
                });
        verify(playerService, times(0)).save(any(Player.class));
    }

    @Test
    void whenUpdateThenReturnOk() {
        when(playerService.update(any(Player.class))).thenAnswer(invocationOnMock -> {
            var player = invocationOnMock.getArgument(0, Player.class);
            return Mono.just(player.toBuilder()
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });
        var playerId = ObjectId.get().toString();
        var requestBody = PlayerFactory.builder().preUpdate(playerId).build();

        webTestClient.put()
                .uri("/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Player.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(requestBody);
                });
        verify(playerService, times(1)).update(any(Player.class));
    }

    @Test
    void whenUpdateWithNonExistingIdThenReturnNotFound() {
        when(playerService.update(any(Player.class))).thenReturn(Mono.error(new NotFoundException("")));
        var playerId = ObjectId.get().toString();
        var requestBody = PlayerFactory.builder().preUpdate(playerId).build();

        webTestClient.put()
                .uri("/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
        verify(playerService, times(1)).update(any(Player.class));
    }

    @Test
    void whenUpdateWithExistingEmailAndNotSameIdThenReturnBadRequest() {
        when(playerService.update(any(Player.class))).thenReturn(Mono.error(new EmailAlreadyUsedException("")));
        var playerId = ObjectId.get().toString();
        var requestBody = PlayerFactory.builder().preUpdate(playerId).build();

        webTestClient.put()
                .uri("/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });
        verify(playerService, times(1)).update(any(Player.class));
    }

    private static Stream<Arguments> whenUpdateWithInvalidConstraintWhenReturnBadRequest() {
        var validId = ObjectId.get().toString();
        var invalidId = faker.lorem().word();
        return Stream.of(
                Arguments.of(invalidId, PlayerRequestFactory.builder().build(), "id"),
                Arguments.of(validId, PlayerRequestFactory.builder().nullName().build(), "name"),
                Arguments.of(validId, PlayerRequestFactory.builder().blankName().build(), "name"),
                Arguments.of(validId, PlayerRequestFactory.builder().shortName().build(), "name"),
                Arguments.of(validId, PlayerRequestFactory.builder().longName().build(), "name"),
                Arguments.of(validId, PlayerRequestFactory.builder().nullEmail().build(), "email"),
                Arguments.of(validId, PlayerRequestFactory.builder().blankEmail().build(), "email"),
                Arguments.of(validId, PlayerRequestFactory.builder().shortEmail().build(), "email"),
                Arguments.of(validId, PlayerRequestFactory.builder().longEmail().build(), "email"),
                Arguments.of(validId, PlayerRequestFactory.builder().invalidEmail().build(), "email")
        );
    }

    @MethodSource
    @ParameterizedTest
    void whenUpdateWithInvalidConstraintWhenReturnBadRequest(final String playerId, final PlayerRequest requestBody, final String field) {
        webTestClient.put()
                .uri("/players/" + playerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains(field);
                });
        verify(playerService, times(0)).update(any(Player.class));
    }

    @Test
    void whenDeleteWhenReturnNoContent() {
        when(playerService.delete(anyString())).thenReturn(Mono.empty());
        var playerId = ObjectId.get().toString();

        webTestClient.delete()
                .uri("/players/" + playerId)
                .exchange()
                .expectStatus().isNoContent();
        verify(playerService, times(1)).delete(anyString());
    }

    @Test
    void whenDeleteWithNonExistingIdWhenReturnNotFound() {
        when(playerService.delete(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var playerId = ObjectId.get().toString();

        webTestClient.delete()
                .uri("/players/" + playerId)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                });
        verify(playerService, times(1)).delete(anyString());
    }

    @Test
    void whenDeleteWithInvalidIdWhenReturnBadRequest() {
        var invalidPlayerId = faker.lorem().word();

        webTestClient.delete()
                .uri("/players/" + invalidPlayerId)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemResponse.class)
                .value(responseBody -> {
                    assertThat(responseBody).isNotNull();
                    assertThat(responseBody.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                    assertThat(responseBody.fields().stream().map(FieldErrorResponse::name).toList()).contains("id");
                });
        verify(playerService, times(0)).delete(anyString());
    }

}
