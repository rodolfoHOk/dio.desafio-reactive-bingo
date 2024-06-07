package me.dio.hiokdev.reactive_bingo.domain.services;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.EmailAlreadyUsedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.gateways.PlayerGateway;
import me.dio.hiokdev.reactive_bingo.domain.services.query.PlayerQueryService;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.adapters.PlayerGatewayImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.PlayerDocumentMapper;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers.PlayerDocumentMapperImpl;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.PlayerDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandPlayerRepositoryImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerDocumentRepository playerDocumentRepository;
    @Mock
    private FindOnDemandPlayerRepositoryImpl findOnDemandPlayerRepository;
    private final PlayerDocumentMapper playerDocumentMapper = new PlayerDocumentMapperImpl();
    private PlayerGateway playerGateway;
    private final Faker faker = FakerData.getFaker();

    @Mock
    private PlayerQueryService playerQueryService;

    private PlayerService playerService;

    @BeforeEach
    void setup() {
        playerGateway = new PlayerGatewayImpl(playerDocumentRepository, findOnDemandPlayerRepository, playerDocumentMapper);
        this.playerService = new PlayerService(playerQueryService, playerGateway);
    }

    @Test
    void whenSaveThenReturnPlayer() {
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(playerDocumentRepository.save(any(PlayerDocument.class))).thenAnswer(invocationOnMock -> {
            var playerDocument = invocationOnMock.getArgument(0, PlayerDocument.class);
            return Mono.just(playerDocument.toBuilder()
                    .id(ObjectId.get().toString())
                    .createdAt(OffsetDateTime.now())
                    .updatedAt(OffsetDateTime.now())
                    .build());
        });
        var player = PlayerFactory.builder().preInsert().build();

        StepVerifier.create(playerService.save(player))
                .assertNext(actual -> {
                    assertThat(actual.id()).isNotNull();
                    assertThat(actual.createdAt()).isNotNull();
                    assertThat(actual.updatedAt()).isNotNull();
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("id", "createdAt", "updatedAt")
                            .isEqualTo(player);
                })
                .verifyComplete();
        verify(playerQueryService).findByEmail(anyString());
        verify(playerDocumentRepository).save(any(PlayerDocument.class));
    }

    @Test
    void whenSaveWithExistingEmailThenThrowEmailAlreadyUsedException() {
        var playerBuilder = PlayerFactory.builder();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.just(playerBuilder.build()));

        StepVerifier.create(playerService.save(playerBuilder.preInsert().build()))
                .verifyError(EmailAlreadyUsedException.class);
        verify(playerQueryService).findByEmail(anyString());
        verify(playerDocumentRepository, times(0)).save(any(PlayerDocument.class));
    }

    @Test
    void whenUpdateWithExistingEmailAndSameIdThenReturnPlayer() {
        var id = ObjectId.get().toString();
        var player = PlayerFactory.builder().preUpdate(id).build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.just(player));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(playerDocumentRepository.save(any(PlayerDocument.class))).thenAnswer(invocationOnMock -> {
            var playerDocument = invocationOnMock.getArgument(0, PlayerDocument.class);
            return Mono.just(playerDocument.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });
        var playerToUpdate = player.toBuilder().name("New Name").build();

        StepVerifier.create(playerService.update(playerToUpdate))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(playerToUpdate);
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isNotEqualTo(player);
                })
                .verifyComplete();
        verify(playerQueryService).findByEmail(anyString());
        verify(playerQueryService).findById(anyString());
        verify(playerDocumentRepository).save(any(PlayerDocument.class));
    }

    @Test
    void whenUpdateWithNonExistingEmailThenReturnPlayer() {
        var id = ObjectId.get().toString();
        var player = PlayerFactory.builder().preUpdate(id).build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(playerDocumentRepository.save(any(PlayerDocument.class))).thenAnswer(invocationOnMock -> {
            var playerDocument = invocationOnMock.getArgument(0, PlayerDocument.class);
            return Mono.just(playerDocument.toBuilder().updatedAt(OffsetDateTime.now()).build());
        });
        var playerToUpdate = player.toBuilder().email("new@email.com").build();

        StepVerifier.create(playerService.update(playerToUpdate))
                .assertNext(actual -> {
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isEqualTo(playerToUpdate);
                    assertThat(actual).usingRecursiveComparison()
                            .ignoringFields("createdAt", "updatedAt")
                            .isNotEqualTo(player);
                })
                .verifyComplete();
        verify(playerQueryService).findByEmail(anyString());
        verify(playerQueryService).findById(anyString());
        verify(playerDocumentRepository).save(any(PlayerDocument.class));
    }

    @Test
    void whenUpdateWithExistingEmailAndNoSameIdThenThrowEmailAlreadyUsedException() {
        var player = PlayerFactory.builder().build();
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.just(player));
        var id = ObjectId.get().toString();
        var playerToUpdate = PlayerFactory.builder().preUpdate(id).build().toBuilder().email(player.email()).build();

        StepVerifier.create(playerService.update(playerToUpdate))
                .verifyError(EmailAlreadyUsedException.class);
        verify(playerQueryService).findByEmail(anyString());
        verify(playerQueryService, times(0)).findById(anyString());
        verify(playerDocumentRepository, times(0)).save(any(PlayerDocument.class));
    }

    @Test
    void whenUpdateWithNonExistingPlayerThenThrowNotFoundException() {
        when(playerQueryService.findByEmail(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));
        var id = ObjectId.get().toString();
        var playerToUpdate = PlayerFactory.builder().preUpdate(id).build();

        StepVerifier.create(playerService.update(playerToUpdate))
                .verifyError(NotFoundException.class);
        verify(playerQueryService).findByEmail(anyString());
        verify(playerQueryService).findById(anyString());
        verify(playerDocumentRepository, times(0)).save(any(PlayerDocument.class));
    }

    @Test
    void whenDeleteWithExistingPlayerThenReturnVoid() {
        var player = PlayerFactory.builder().build();
        when(playerQueryService.findById(anyString())).thenReturn(Mono.just(player));
        when(playerDocumentRepository.delete(any(PlayerDocument.class))).thenReturn(Mono.empty());

        StepVerifier.create(playerService.delete(player.id())).verifyComplete();
        verify(playerQueryService).findById(anyString());
        verify(playerDocumentRepository).delete(any(PlayerDocument.class));
    }

    @Test
    void whenDeleteWithNonExistingPlayerThenThrowNotFoundException() {
        var player = PlayerFactory.builder().build();
        when(playerQueryService.findById(anyString())).thenReturn(Mono.error(new NotFoundException("")));

        StepVerifier.create(playerService.delete(player.id())).verifyError(NotFoundException.class);
        verify(playerQueryService).findById(anyString());
        verify(playerDocumentRepository, times(0)).delete(any(PlayerDocument.class));
    }

}
