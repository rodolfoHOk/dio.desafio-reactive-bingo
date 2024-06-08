package me.dio.hiokdev.reactive_bingo.domain.services;

import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.RoundFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.MailMessage;
import me.dio.hiokdev.reactive_bingo.domain.enums.RoundState;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BingoCardAlreadyExistsException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyFinishedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.RoundGateway;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.domain.services.query.RoundQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters.RoundGatewayImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RoundServiceTest {

    @Mock
    private RoundDocumentRepository roundDocumentRepository;
    @Mock
    private FindOnDemandRoundRepositoryImpl findOnDemandRoundRepository;
    @Spy
    private BingoCardSubDocumentMapper bingoCardSubDocumentMapper = new BingoCardSubDocumentMapperImpl();
    @InjectMocks
    private RoundDocumentMapper roundDocumentMapper = new RoundDocumentMapperImpl();

    @Mock
    private PlayerQueryService playerQueryService;
    @Mock
    private RoundQueryService roundQueryService;
    @Mock
    private MailService mailService;

    private RoundService roundService;

    @BeforeEach
    void setup() {
        RoundGateway roundGateway = new RoundGatewayImpl(roundDocumentRepository, findOnDemandRoundRepository, roundDocumentMapper);
        this.roundService = new RoundService(playerQueryService, roundQueryService, roundGateway, mailService);
    }

    @Test
    void whenCreateThenReturnRound() {
        when(roundDocumentRepository.save(any(RoundDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, RoundDocument.class);
            return Mono.just(document.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });

        StepVerifier.create(roundService.create())
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual.id()).isNotNull();
                    assertThat(actual.createdAt()).isNotNull();
                    assertThat(actual.updatedAt()).isNotNull();
                    assertThat(actual.state()).isEqualTo(RoundState.CREATED);
                    assertThat(actual.bingoCards()).isEmpty();
                    assertThat(actual.sortedNumbers()).isEmpty();
                    assertThat(actual.winnersIds()).isEmpty();
                })
                .verifyComplete();
        verify(roundDocumentRepository).save(any(RoundDocument.class));
    }

    @Test
    void whenGenerateBingoCardWithNonExistingByPlayerThenReturnBingoCard() {
        var player = PlayerFactory.builder().build();
        var round = RoundFactory.builder().build();
        when(roundQueryService.verifyIfExistsByIdAndPlayerId(anyString(), anyString())).thenReturn(Mono.empty());
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundDocumentRepository.save(any(RoundDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, RoundDocument.class);
            return Mono.just(document.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });

        StepVerifier.create(roundService.generateBingoCard(round.id(), player.id()))
                .assertNext(actual -> {
                    assertThat(actual.id()).isNotNull();
                    assertThat(actual.player()).isEqualTo(player);
                    assertThat(actual.numbers().size()).isEqualTo(20);
                    assertThat(actual.hintCount()).isEqualTo(0);
                    assertThat(actual.createdAt()).isNotNull();
                    assertThat(actual.updatedAt()).isNotNull();
                })
                .verifyComplete();
        verify(roundQueryService).verifyIfExistsByIdAndPlayerId(anyString(), anyString());
        verify(playerQueryService).findById(anyString());
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository).save(any(RoundDocument.class));
    }

    @Test
    void whenGenerateBingoCardWithRoundInitiatedThenThrowRoundAlreadyInitiatedException() {
        var player = PlayerFactory.builder().build();
        var round = RoundFactory.builder().build().toBuilder().state(RoundState.INITIATED).build();
        when(roundQueryService.verifyIfExistsByIdAndPlayerId(anyString(), anyString())).thenReturn(Mono.empty());
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));

        StepVerifier.create(roundService.generateBingoCard(round.id(), player.id()))
                .verifyError(RoundAlreadyInitiatedException.class);
        verify(roundQueryService).verifyIfExistsByIdAndPlayerId(anyString(), anyString());
        verify(playerQueryService).findById(anyString());
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository, times(0)).save(any(RoundDocument.class));
    }

    @Test
    void whenGenerateBingoCardWithNonExistingRoundIdThenThrowNotFoundException() {
        var player = PlayerFactory.builder().build();
        when(roundQueryService.verifyIfExistsByIdAndPlayerId(anyString(), anyString())).thenReturn(Mono.empty());
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(roundQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var nonExistingRoundId = ObjectId.get().toString();

        StepVerifier.create(roundService.generateBingoCard(nonExistingRoundId, player.id()))
                .verifyError(NotFoundException.class);
        verify(roundQueryService).verifyIfExistsByIdAndPlayerId(anyString(), anyString());
        verify(playerQueryService).findById(anyString());
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository, times(0)).save(any(RoundDocument.class));
    }

    @Test
    void whenGenerateBingoCardWithNonExistingPlayerIdThenThrowNotFoundException() {
        when(roundQueryService.verifyIfExistsByIdAndPlayerId(anyString(), anyString())).thenReturn(Mono.empty());
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var roundId = ObjectId.get().toString();
        var nonExistingPlayerId = ObjectId.get().toString();

        StepVerifier.create(roundService.generateBingoCard(roundId, nonExistingPlayerId))
                .verifyError(NotFoundException.class);
        verify(roundQueryService).verifyIfExistsByIdAndPlayerId(anyString(), anyString());
        verify(playerQueryService).findById(anyString());
        verify(roundQueryService, times(0)).findById(anyString());
        verify(roundDocumentRepository, times(0)).save(any(RoundDocument.class));
    }

    @Test
    void whenGenerateBingoCardWithExistingPlayerBingoCardThenThrowBingoCardAlreadyExistsException() {
        when(roundQueryService.verifyIfExistsByIdAndPlayerId(anyString(), anyString()))
                .thenReturn(Mono.error(new BingoCardAlreadyExistsException("")));
        var roundId = ObjectId.get().toString();
        var playerId = ObjectId.get().toString();

        StepVerifier.create(roundService.generateBingoCard(roundId, playerId))
                .verifyError(BingoCardAlreadyExistsException.class);
        verify(roundQueryService).verifyIfExistsByIdAndPlayerId(anyString(), anyString());
        verify(playerQueryService, times(0)).findById(anyString());
        verify(roundQueryService, times(0)).findById(anyString());
        verify(roundDocumentRepository, times(0)).save(any(RoundDocument.class));
    }

    @Test
    void whenGenerateNextNumberWithoutSortedNumberThenReturnInteger() {
        var round = RoundFactory.builder().build();
        round = RoundFactory.generateCards(10, round);
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundDocumentRepository.save(any(RoundDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, RoundDocument.class);
            return Mono.just(document.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });

        StepVerifier.create(roundService.generateNextNumber(round.id()))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).isPositive();
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository).save(any(RoundDocument.class));
        verifyNoInteractions(mailService);
    }

    @Test
    void whenGenerateNextNumberWithExistingSortedNumbersThenReturnInteger() {
        var round = RoundFactory.builder().build();
        round = RoundFactory.generateCards(10, round);
        round = RoundFactory.generateSortedNumbers(30, round);
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundDocumentRepository.save(any(RoundDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, RoundDocument.class);
            return Mono.just(document.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });

        StepVerifier.create(roundService.generateNextNumber(round.id()))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).isPositive();
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository).save(any(RoundDocument.class));
        verifyNoInteractions(mailService);
    }

    @Test
    void whenGenerateNextNumberWithRoundedIsFinishedThenThrowRoundAlreadyFinishedException() {
        var round = RoundFactory.createFinishedRound();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));

        StepVerifier.create(roundService.generateNextNumber(round.id()))
                        .verifyError(RoundAlreadyFinishedException.class);
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository, times(0)).save(any(RoundDocument.class));
        verifyNoInteractions(mailService);
    }

    @Test
    void whenGenerateNextNumberWithNonExistingRoundedThenThrowNotFoundException() {
        when(roundQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var nonExistingRoundId = ObjectId.get().toString();

        StepVerifier.create(roundService.generateNextNumber(nonExistingRoundId))
                .verifyError(NotFoundException.class);
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository, times(0)).save(any(RoundDocument.class));
        verifyNoInteractions(mailService);
    }

    @Test
    void whenGenerateNextNumberThenReturnIntegerAndSendEmails() throws InterruptedException {
        var round = RoundFactory.createRoundToFinish();
        when(roundQueryService.findById(anyString())).thenReturn(Mono.just(round));
        when(roundDocumentRepository.save(any(RoundDocument.class))).thenAnswer(invocationOnMock -> {
            var document = invocationOnMock.getArgument(0, RoundDocument.class);
            return Mono.just(document.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(round.bingoCards().getFirst().player()));
        when(mailService.send(any(MailMessage.class))).thenReturn(Mono.empty());

        StepVerifier.create(roundService.generateNextNumber(round.id()))
                .assertNext(actual -> {
                    assertThat(actual).isNotNull();
                    assertThat(actual).isPositive();
                })
                .verifyComplete();
        verify(roundQueryService).findById(anyString());
        verify(roundDocumentRepository).save(any(RoundDocument.class));
        TimeUnit.SECONDS.sleep(2);
        verify(playerQueryService, times(round.bingoCards().size())).findById(anyString());
        verify(mailService, times(round.bingoCards().size())).send(any(MailMessage.class));
    }

}
