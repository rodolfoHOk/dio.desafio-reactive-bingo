package me.dio.hiokdev.reactive_bingo.domain.services.query;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.document.PlayerDocumentFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.dto.PageablePlayersFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.request.PageablePlayersRequestFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.PlayerGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters.PlayerGatewayImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.PlayerDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.PlayerDocumentMapperImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.PlayerDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandPlayerRepositoryImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PlayerQueryServiceTest {

    @Mock
    private PlayerDocumentRepository playerDocumentRepository;
    @Mock
    private FindOnDemandPlayerRepositoryImpl findOnDemandPlayerRepository;
    private final PlayerDocumentMapper playerDocumentMapper = new PlayerDocumentMapperImpl();
    private PlayerQueryService playerQueryService;
    private static final Faker faker = FakerData.getFaker();

    @BeforeEach
    void setup() {
        PlayerGateway playerGateway = new PlayerGatewayImpl(playerDocumentRepository, findOnDemandPlayerRepository, playerDocumentMapper);
        this.playerQueryService = new PlayerQueryService(playerGateway);
    }

    @Test
    void whenFindByIdThenReturnPlayer() {
        var player = PlayerFactory.builder().build();
        var playerDocument = PlayerDocumentFactory.builder(player).build();
        when(playerDocumentRepository.findById(anyString())).thenReturn(Mono.just(playerDocument));

        StepVerifier.create(playerQueryService.findById(playerDocument.id()))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .isEqualTo(player);
                })
                .verifyComplete();
        verify(playerDocumentRepository).findById(anyString());
        verifyNoInteractions(findOnDemandPlayerRepository);
    }

    @Test
    void whenFindByIdWithNonStoredPlayerByIdThenThrowNotFoundException() {
        var id = ObjectId.get().toString();
        when(playerDocumentRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(playerQueryService.findById(id))
                .verifyError(NotFoundException.class);
        verify(playerDocumentRepository).findById(anyString());
        verifyNoInteractions(findOnDemandPlayerRepository);
    }

    @Test
    void whenFindByEmailThenReturnPlayer() {
        var player = PlayerFactory.builder().build();
        var playerDocument = PlayerDocumentFactory.builder(player).build();
        when(playerDocumentRepository.findByEmail(anyString())).thenReturn(Mono.just(playerDocument));

        StepVerifier.create(playerQueryService.findByEmail(playerDocument.email()))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .isEqualTo(player);
                })
                .verifyComplete();
        verify(playerDocumentRepository).findByEmail(anyString());
        verifyNoInteractions(findOnDemandPlayerRepository);
    }

    @Test
    void whenFindByEmailWithNonStoredPlayerByEmailThenThrowNotFoundException() {
        var email = faker.internet().emailAddress();
        when(playerDocumentRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(playerQueryService.findByEmail(email))
                .verifyError(NotFoundException.class);
        verify(playerDocumentRepository).findByEmail(anyString());
        verifyNoInteractions(findOnDemandPlayerRepository);
    }

    private static Stream<Arguments> whenFindOnDemandThenReturnPagedPlayers() {
        var pageableRequest = PageablePlayersRequestFactory.builder().build();
        var pageable = PageablePlayersFactory.builder(pageableRequest).build();
        var players = Stream.generate(() -> PlayerFactory.builder().build())
                .limit(faker.number().randomDigitNotZero()).toList();
        var total = faker.number().numberBetween(players.size(), players.size() * 3L);
        var totalPages = total / pageable.limit() + ((total % pageable.limit() > 0) ? 1 : 0);
        return Stream.of(
            Arguments.of(pageable, players, total, totalPages),
            Arguments.of(pageable, List.of(), 0L, 0L)
        );
    }

    @MethodSource
    @ParameterizedTest
    void whenFindOnDemandThenReturnPagedPlayers(
            final PageablePlayers pageable,
            final List<Player> players,
            final Long total,
            final Long totalPages
    ) {
        var documents = players.stream().map(player -> PlayerDocumentFactory.builder(player).build()).toList();
        when(findOnDemandPlayerRepository.findOnDemand(any(PageablePlayers.class))).thenReturn(Flux.fromIterable(documents));
        when(findOnDemandPlayerRepository.count(any(PageablePlayers.class))).thenReturn(Mono.just(total));

        StepVerifier.create(playerQueryService.findOnDemand(pageable))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.totalItens()).isEqualTo(total);
                    assertThat(actual.totalPages()).isEqualTo(totalPages);
                    assertThat(actual.content()).containsExactlyInAnyOrderElementsOf(players);
                })
                .verifyComplete();
        verify(findOnDemandPlayerRepository).findOnDemand(any(PageablePlayers.class));
        verify(findOnDemandPlayerRepository).count(any(PageablePlayers.class));
        verifyNoInteractions(playerDocumentRepository);
    }

}
