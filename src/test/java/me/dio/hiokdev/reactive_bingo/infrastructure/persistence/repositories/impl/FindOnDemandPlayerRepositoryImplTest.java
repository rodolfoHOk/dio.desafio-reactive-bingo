package me.dio.hiokdev.reactive_bingo.infrastructure.persistence.repositories.impl;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.core.config.MongoDBTestConfig;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.core.factory.document.PlayerDocumentFactory;
import me.dio.hiokdev.reactive_bingo.core.factory.domain.PlayerFactory;
import me.dio.hiokdev.reactive_bingo.domain.dto.PageablePlayers;
import me.dio.hiokdev.reactive_bingo.domain.enums.PlayerSortBy;
import me.dio.hiokdev.reactive_bingo.domain.enums.SortDirection;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.PlayerDocumentRepository;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.repositories.impl.FindOnDemandPlayerRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MongoDBTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FindOnDemandPlayerRepositoryImplTest {

    @Autowired
    private PlayerDocumentRepository playerDocumentRepository;
    @Autowired
    private FindOnDemandPlayerRepositoryImpl findOnDemandPlayerRepository;
    private static final Faker faker = FakerData.getFaker();
    private final List<PlayerDocument> storedDocuments = new ArrayList<>();

    @BeforeEach
    void setup() {
        var players = Stream.generate(() -> PlayerFactory.builder().build()).limit(15).toList();
        var playersDocuments = players.stream().map(player -> PlayerDocumentFactory.builder(player).build()).toList();
        var savedPlayers = playerDocumentRepository.saveAll(playersDocuments).collectList().block();
        storedDocuments.addAll(Objects.requireNonNull(savedPlayers));
    }

    @AfterEach
    void tearDown() {
        playerDocumentRepository.deleteAll().block();
        storedDocuments.clear();
    }

    @Test
    void whenFindOnDemandFilterBySentenceThenReturnPlayers() {
        var selectRandomDocument = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectRandomDocument.name().substring(1, 4)
                : selectRandomDocument.email().substring(1, 4);
        var pageable = PageablePlayers.builder().sentence(sentence).build();

        StepVerifier.create(findOnDemandPlayerRepository.findOnDemand(pageable))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var expectedSize = storedDocuments.stream()
                            .filter(document -> document.name().contains(sentence) || document.email().contains(sentence))
                            .count();
                    assertThat(actual.size()).isEqualTo(expectedSize);
                    var actualList = new ArrayList<>(actual);
                    assertThat(actualList).isSortedAccordingTo(Comparator.comparing(PlayerDocument::name));
                })
                .verifyComplete();
    }

    @Test
    void whenCountFilterBySentenceThenReturnInteger() {
        var selectRandomDocument = storedDocuments.get(faker.number().numberBetween(0, storedDocuments.size()));
        var sentence = faker.bool().bool() ? selectRandomDocument.name().substring(1, 4).trim()
                : selectRandomDocument.email().substring(1, 4).trim();
        var pageable = PageablePlayers.builder().sentence(sentence).build();

        StepVerifier.create(findOnDemandPlayerRepository.count(pageable))
                .assertNext(actual -> {
                    var expectedSize = storedDocuments.stream()
                            .filter(document -> document.name().contains(sentence) || document.email().contains(sentence))
                            .count();
                    assertThat(actual).isEqualTo(expectedSize);
                })
                .verifyComplete();
    }

    private static Stream<Arguments> whenFindOnDemandSortByThenReturnPlayersInOrder() {
        return Stream.of(
                Arguments.of(
                    PageablePlayers.builder().sortBy(PlayerSortBy.NAME).sortDirection(SortDirection.ASC).build(),
                    Comparator.comparing(PlayerDocument::name)
                ),
                Arguments.of(
                        PageablePlayers.builder().sortBy(PlayerSortBy.NAME).sortDirection(SortDirection.DESC).build(),
                        Comparator.comparing(PlayerDocument::name).reversed()
                ),
                Arguments.of(
                        PageablePlayers.builder().sortBy(PlayerSortBy.EMAIL).sortDirection(SortDirection.ASC).build(),
                        Comparator.comparing(PlayerDocument::email)
                ),
                Arguments.of(
                        PageablePlayers.builder().sortBy(PlayerSortBy.EMAIL).sortDirection(SortDirection.DESC).build(),
                        Comparator.comparing(PlayerDocument::email).reversed()
                )
        );
    }

    @MethodSource
    @ParameterizedTest
    void whenFindOnDemandSortByThenReturnPlayersInOrder(final PageablePlayers pageable, final Comparator<PlayerDocument> comparator) {
        StepVerifier.create(findOnDemandPlayerRepository.findOnDemand(pageable))
                .recordWith(ArrayList::new)
                .thenConsumeWhile(actual -> true)
                .consumeRecordedWith(actual -> {
                    var actualList = new ArrayList<>(actual);
                    assertThat(actualList).isSortedAccordingTo(comparator);
                })
                .verifyComplete();
    }

}
