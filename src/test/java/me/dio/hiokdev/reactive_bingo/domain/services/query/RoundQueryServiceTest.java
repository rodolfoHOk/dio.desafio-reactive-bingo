package me.dio.hiokdev.reactive_bingo.domain.services.query;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.document.RoundDocumentFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.dto.PageableRoundsFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.request.PageableRoundsRequestFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageableRounds;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BingoCardAlreadyExistsException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundNotInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters.RoundGatewayImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.BingoCardSubDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.BingoCardSubDocumentMapperImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.RoundDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.RoundDocumentMapperImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.RoundDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandRoundRepositoryImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
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
public class RoundQueryServiceTest {

    @Mock
    private RoundDocumentRepository roundDocumentRepository;
    @Mock
    private FindOnDemandRoundRepositoryImpl findOnDemandRoundRepository;
    @Spy
    private BingoCardSubDocumentMapper bingoCardSubDocumentMapper = new BingoCardSubDocumentMapperImpl();
    @InjectMocks
    private RoundDocumentMapper roundDocumentMapper = new RoundDocumentMapperImpl();

    private RoundQueryService roundQueryService;
    private static final Faker faker = FakerData.getFaker();

    @BeforeEach
    void setup() {
        RoundGateway roundGateway = new RoundGatewayImpl(roundDocumentRepository, findOnDemandRoundRepository, roundDocumentMapper);
        this.roundQueryService = new RoundQueryService(roundGateway);
    }

    @Test
    void whenFindByIdThenReturnRound() {
        var round = RoundFactory.builder().build();
        var roundDocument = RoundDocumentFactory.builder(round).build();
        when(roundDocumentRepository.findById(anyString())).thenReturn(Mono.just(roundDocument));

        StepVerifier.create(roundQueryService.findById(roundDocument.id()))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .isEqualTo(round);
                })
                .verifyComplete();
        verify(roundDocumentRepository).findById(anyString());
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenFindByIdWithNonStoredRoundByIdThenThrowNotFoundException() {
        var id = ObjectId.get().toString();
        when(roundDocumentRepository.findById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(roundQueryService.findById(id))
                .verifyError(NotFoundException.class);
        verify(roundDocumentRepository).findById(anyString());
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenVerifyIfExistsByIdAndPlayerIdThenNotThrowAnException() {
        var id = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();
        when(roundDocumentRepository.existsByIdAndBingoCards_Player_Id(anyString(), anyString())).thenReturn(Mono.just(Boolean.FALSE));

        StepVerifier.create(roundQueryService.verifyIfExistsByIdAndPlayerId(id, playerId))
                .verifyComplete();
        verify(roundDocumentRepository).existsByIdAndBingoCards_Player_Id(anyString(), anyString());
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenVerifyIfExistsByIdAndPlayerIdThenThrowBingoCardAlreadyExistsException() {
        var id = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();
        when(roundDocumentRepository.existsByIdAndBingoCards_Player_Id(anyString(), anyString())).thenReturn(Mono.just(Boolean.TRUE));

        StepVerifier.create(roundQueryService.verifyIfExistsByIdAndPlayerId(id, playerId))
                .verifyError(BingoCardAlreadyExistsException.class);
        verify(roundDocumentRepository).existsByIdAndBingoCards_Player_Id(anyString(), anyString());
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenGetLastSortedNumberThenReturnInteger() {
        Round round = RoundFactory.builder().build();
        var player1 = PlayerFactory.builder().build();
        var player2 = PlayerFactory.builder().build();
        round = round.toBuilder().addBingoCard(player1).block().build();
        round = round.toBuilder().addBingoCard(player2).block().build();
        round = round.toBuilder().sortNumber().block().build();
        round = round.toBuilder().sortNumber().block().build();
        round = round.toBuilder().sortNumber().block().build();
        var lastSortedNumber = round.sortedNumbers().get(2);
        var roundDocument = RoundDocumentFactory.builder(round).build();
        when(roundDocumentRepository.findById(anyString())).thenReturn(Mono.just(roundDocument));

        StepVerifier.create(roundQueryService.getLastSortedNumber(round.id()))
                .assertNext(actual -> assertThat(actual).isEqualTo(lastSortedNumber))
                .verifyComplete();
        verify(roundDocumentRepository).findById(anyString());
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenGetLastSortedNumberThenThrowRoundNotInitiatedException() {
        Round round = RoundFactory.builder().build();
        var player1 = PlayerFactory.builder().build();
        var player2 = PlayerFactory.builder().build();
        round = round.toBuilder().addBingoCard(player1).block().build();
        round = round.toBuilder().addBingoCard(player2).block().build();
        var roundDocument = RoundDocumentFactory.builder(round).build();
        when(roundDocumentRepository.findById(anyString())).thenReturn(Mono.just(roundDocument));

        StepVerifier.create(roundQueryService.getLastSortedNumber(round.id()))
                .verifyError(RoundNotInitiatedException.class);
        verify(roundDocumentRepository).findById(anyString());
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenFindAllThenReturnFluxOfRounds() {
        var rounds = Stream.generate(() -> RoundFactory.builder().build())
                .limit(faker.number().randomDigitNotZero()).toList();
        var documents = rounds.stream().map(round -> RoundDocumentFactory.builder(round).build()).toList();
        when(roundDocumentRepository.findAll()).thenReturn(Flux.fromIterable(documents));

        StepVerifier.create(roundQueryService.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    assertThat(actual.size()).isEqualTo(documents.size());
                    assertThat(actual).containsAll(rounds);
                })
                .verifyComplete();
        verify(roundDocumentRepository).findAll();
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    @Test
    void whenFindAllThenReturnFluxEmpty() {
        when(roundDocumentRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(roundQueryService.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    assertThat(actual.size()).isEqualTo(0);
                })
                .verifyComplete();
        verify(roundDocumentRepository).findAll();
        verifyNoInteractions(findOnDemandRoundRepository);
    }

    private static Stream<Arguments> whenFindOnDemandThenReturnPagedRounds() {
        var pageableRequest = PageableRoundsRequestFactory.builder().build();
        var pageable = PageableRoundsFactory.builder(pageableRequest).build();
        var rounds = Stream.generate(() -> RoundFactory.builder().build())
                .limit(faker.number().randomDigitNotZero()).toList();
        var total = faker.number().numberBetween(rounds.size(), rounds.size() * 3L);
        var totalPages = total / pageable.limit() + ((total % pageable.limit() > 0) ? 1 : 0);
        return Stream.of(
                Arguments.of(pageable, rounds, total, totalPages),
                Arguments.of(pageable, List.of(), 0L, 0L)
        );
    }

    @MethodSource
    @ParameterizedTest
    void whenFindOnDemandThenReturnPagedRounds(
            final PageableRounds pageable,
            final List<Round> rounds,
            final Long total,
            final Long totalPages
    ) {
        var documents = rounds.stream().map(round -> RoundDocumentFactory.builder(round).build()).toList();
        when(findOnDemandRoundRepository.findOnDemand(any(PageableRounds.class))).thenReturn(Flux.fromIterable(documents));
        when(findOnDemandRoundRepository.count(any(PageableRounds.class))).thenReturn(Mono.just(total));

        StepVerifier.create(roundQueryService.findOnDemand(pageable))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.totalItens()).isEqualTo(total);
                    assertThat(actual.totalPages()).isEqualTo(totalPages);
                    assertThat(actual.content()).containsExactlyInAnyOrderElementsOf(rounds);
                })
                .verifyComplete();
        verify(findOnDemandRoundRepository).findOnDemand(any(PageableRounds.class));
        verify(findOnDemandRoundRepository).count(any(PageableRounds.class));
        verifyNoInteractions(roundDocumentRepository);
    }

}
